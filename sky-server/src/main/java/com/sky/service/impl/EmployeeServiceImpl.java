package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 添加员工
     * @param employeeDTO
     */
    @Override
    public  void save(EmployeeDTO employeeDTO) {
        //创建一个Employee对象
        Employee employee=new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置账号的状态  默认为启用
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码  默认为加密后的123456
        employee.setPassword(DigestUtils.md5DigestAsHex((PasswordConstant.DEFAULT_PASSWORD).getBytes()));
        //设置创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //设置修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置创建人
//        employee.setCreateUser(BaseContext.getCurrentId());
//        //设置修改时间
//        employee.setUpdateUser(BaseContext.getCurrentId());
        //调用mapper层
        employeeMapper.insert(employee);
    }

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 后期需要进行md5加密，然后再进行比对
        //将前端传过来的密码进行md5加密后与数据库查询出来的密码进行比对
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        //获取当前页码
        int page = employeePageQueryDTO.getPage();
        //获取每页显示的条数
        int pageSize = employeePageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        //调用mapper层进行分页查询
        Page<Employee> employeePage=employeeMapper.page(employeePageQueryDTO);
        //将employeePage封装成pageResult
        long total = employeePage.getTotal();
        List<Employee> result = employeePage.getResult();
        return new PageResult(total,result);
    }

    /**
     * 启用禁用员工
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //采用建造者模式创建对象
        Employee employee = Employee.builder().id(id).status(status).build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee=employeeMapper.getById(id);
        return employee;
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employeeMapper.update(employee);
    }

}
