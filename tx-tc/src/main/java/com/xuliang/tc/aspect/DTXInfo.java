package com.xuliang.tc.aspect;


import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.aspect.enums.DTXPropagation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Date: 19-1-11 下午1:21
 *
 * @author xuliang
 */
@AllArgsConstructor
@Data
public class DTXInfo {


    private static final Map<String, DTXInfo> dtxInfoCache
            = new ConcurrentReferenceHashMap<>();

    /**
     * 事务类型
     */
    private String transactionType;

    /**
     * 事物传播级别
     */
    private DTXPropagation dtxPropagation;

    private TransactionInfo transactionInfo;

    /**
     * 用户实例对象的业务方法（包含注解信息）
     */
    private Method businessMethod;

    private String unitId;

    private DTXInfo(Method method, Object[] args, Class<?> targetClass) {
        this.transactionInfo = new TransactionInfo();
        this.transactionInfo.setTargetClazz(targetClass);
        this.transactionInfo.setArgumentValues(args);
        this.transactionInfo.setMethod(method.getName());
        this.transactionInfo.setMethodStr(method.toString());
        this.transactionInfo.setParameterTypes(method.getParameterTypes());
        this.businessMethod = method;
        this.unitId = Transactions.unitId(method.toString());
    }

    private void reanalyseMethodArgs(Object[] args) {
        this.transactionInfo.setArgumentValues(args);
    }


    public static DTXInfo getFromCache(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //获取签名
        String signature = proceedingJoinPoint.getSignature().toString();
        //根据签名构造事物单元 单元id
        String unitId = Transactions.unitId(signature);
        //根据事物单元id  获取事物信息
        DTXInfo dtxInfo = dtxInfoCache.get(unitId);
        if (Objects.isNull(dtxInfo)) {
            //获取签名
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            //获取接口的方法
            Method method = methodSignature.getMethod();
            //获取目标方法
            Class<?> targetClass = proceedingJoinPoint.getTarget().getClass();
            //这才是我们要的真实方法
            Method thisMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            //构造
            dtxInfo = new DTXInfo(thisMethod, proceedingJoinPoint.getArgs(), targetClass);
            //缓存起来
            dtxInfoCache.put(unitId, dtxInfo);
        }

        dtxInfo.reanalyseMethodArgs(proceedingJoinPoint.getArgs());
        return dtxInfo;
    }


    /**
     * 从缓存中获取DTXInfo
     */
    public static DTXInfo getFromCache(MethodInvocation methodInvocation) {
        String signature = methodInvocation.getMethod().toString();//方法名
        String unitId = Transactions.unitId(signature);
        DTXInfo dtxInfo = dtxInfoCache.get(unitId);
        if (Objects.isNull(dtxInfo)) {
            dtxInfo = new DTXInfo(methodInvocation.getMethod(),
                    methodInvocation.getArguments(), methodInvocation.getThis().getClass());
            dtxInfoCache.put(unitId, dtxInfo);
        }
        dtxInfo.reanalyseMethodArgs(methodInvocation.getArguments());
        return dtxInfo;
    }



}
