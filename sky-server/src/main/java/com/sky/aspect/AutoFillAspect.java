package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

//实现自动填充字段的切面类
@Component   //spring能识别的bean
@Aspect   //定义为切面类
@Slf4j
public class AutoFillAspect {
    //定义切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //定义前置通知
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException { //连接点相当于方法
        log.info("开始进行公共字段自动填充");
        //先获取方法要进行的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//1.获取方法签名
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//2.获取注解
        OperationType operationType = annotation.value();//3.获取注解的值
        //在获取方法的实体参数，因为要为实体的公共属性赋值
        Object[] args = joinPoint.getArgs();
        if(args==null|args.length==0){
            return;
        }
        Object entity = args[0];
        //准备公共属性的值
        LocalDateTime now = LocalDateTime.now();//获取当前时间
        Long currentId = BaseContext.getCurrentId();//获取当前登录的用户id
        //判断数据库操作类型，对应做出相应的赋值策略
        if(operationType==OperationType.INSERT){
            //插入操作，赋值4个公共属性
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);//获取方法对象
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,currentId);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }else if(operationType==OperationType.UPDATE){
            //更新操作，赋值2个公共属性
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }
    }
}
