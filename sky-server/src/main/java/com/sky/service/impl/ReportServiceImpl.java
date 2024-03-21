package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 查询指定时间段内每天的营业额
     * @param begin
     * @param end
     * @return
     */

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //查询时间段的时间列表
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);//先将第一天的数据加入集合中
         while(!begin.equals(end)){//直到最后一天
             begin=begin.plusDays(1);//下一天
             dateList.add(begin);//加入集合中
         }
         //将集合转换成String类型
        String dateList2 = StringUtils.join(dateList, ',');
        //查询时间段的每天营业额的列表(营业额：订单状态为完成状态）
        //遍历时间列表集合
        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //为了与后台的create_time日期格式相匹配，进行日期格式化
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //将条件进行封装->map
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double sum=ordersMapper.getTurnoverByMap(map);
            //查询出来有可能今天的营业额为0，那么sum==null，为了防止出现空指针异常，采取以下措施
            sum=sum==null?0.0:sum;
            turnoverList.add(sum);
        }
        String turnoverList2 = StringUtils.join(turnoverList, ',');
        //返回数据
        return TurnoverReportVO.builder()
                .dateList(dateList2)
                .turnoverList(turnoverList2)
                .build();
    }
    /**
     * 查询指定时间段内总用户数量和每天新增用户
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //查询时间段的时间列表
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);//先将第一天的数据加入集合中
        while(!begin.equals(end)){//直到最后一天
            begin=begin.plusDays(1);//下一天
            dateList.add(begin);//加入集合中
        }
        //将集合转换成String类型
        String dateList2 = StringUtils.join(dateList, ',');
        List<Integer> totalUserList=new ArrayList<>();
        List<Integer> newUserList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //为了与后台的create_time日期格式相匹配，进行日期格式化
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //将条件进行封装->map
            //查询时间段的每天总用户的列表
            Map map=new HashMap();
            map.put("end",endTime);
            //select count(id) from user where create_time<?
            Integer totalUser=userMapper.getUserNumByMap(map);
            totalUserList.add(totalUser);
            //查询时间段的每天新用户的列表
            map.put("begin",beginTime);
            //select count(id) from user where create_time>? and create_time<?
            Integer newUser=userMapper.getUserNumByMap(map);
            newUserList.add(newUser);
        }
        String totalUserList2 = StringUtils.join(totalUserList, ',');
        String newUserList2 = StringUtils.join(newUserList, ',');
        return UserReportVO.builder()
                .dateList(dateList2)
                .totalUserList(totalUserList2)
                .newUserList(newUserList2)
                .build();
    }
    /**
     * 查询指定时间段内每天的总订单和有效订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //查询时间段的时间列表
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);//先将第一天的数据加入集合中
        while(!begin.equals(end)){//直到最后一天
            begin=begin.plusDays(1);//下一天
            dateList.add(begin);//加入集合中
        }
        //将集合转换成String类型
        List<Integer> totalOrderList=new ArrayList<>();
        List<Integer> validOrderList=new ArrayList<>();
        String dateList2 = StringUtils.join(dateList, ',');
        for (LocalDate localDate : dateList) {
            //为了与后台的create_time日期格式相匹配，进行日期格式化
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //将条件进行封装->map
            //查询每天的总订单数
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer totalOrder=ordersMapper.getOrderNumByMap(map);
            totalOrderList.add(totalOrder);
            //select count(id) from orders where order_time>? and order_time<?;
            //查询每天的有效订单数（已完成的订单）
            map.put("status",Orders.COMPLETED);
            //select count(id) from orders where order_time>? and order_time<? and status=5;
            Integer validOrder=ordersMapper.getOrderNumByMap(map);
            validOrderList.add(validOrder);
        }
        String totalOrderList2 = StringUtils.join(totalOrderList, ',');
        String validOrderList2 = StringUtils.join(validOrderList, ',');
        //获取时间段内的所有订单总数
        Integer totals = totalOrderList.stream().reduce(Integer::sum).get();
        //获取时间段内的有效订单总数
        Integer valids = validOrderList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double rate=0.0;
        if (totals != 0) {//注意分母不能为零
            //获取有效订单总数的double值
            rate=(valids.doubleValue())/totals;
        }
        return OrderReportVO.builder()
                .dateList(dateList2)
                .orderCountList(totalOrderList2)
                .validOrderCountList(validOrderList2)
                .totalOrderCount(totals)
                .validOrderCount(valids)
                .orderCompletionRate(rate)
                .build();
    }
    /**
     * 查询指定时间段内销量前十的菜品或者套餐
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //获取销量前十的商品列表(订单已经为完成状态）
        List<GoodsSalesDTO> list=ordersMapper.getSalesTop10List(beginTime,endTime);
        for (GoodsSalesDTO goodsSalesDTO : list) {
            System.out.println(goodsSalesDTO.getName()+":"+goodsSalesDTO.getNumber());
        }
        //获取所有的商品名称的列表
        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList2= StringUtils.join(nameList, ',');
        //获取销量前十的商品的销量
        List<Integer> numberList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList2= StringUtils.join(numberList, ',');
        return SalesTop10ReportVO.builder()
                .nameList(nameList2)
                .numberList(numberList2)
                .build();
    }
    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");//获取输入流

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
