package com.xuliang.tc.core.context;


import com.xuliang.tc.aspect.TransactionInfo;
import com.xuliang.tc.enums.DTXPropagation;
import com.xuliang.tc.aspect.callback.BusinessCallback;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 切面控制对象
 * Created by xuliang on 2017/6/8.
 */
@Data
public class TxTransactionInfo {


    private String transactionType;

    /**
     * 事务发起方
     */
    private boolean transactionStart;

    /**
     * 事务组标识id
     */
    private String groupId;


    /**
     * 事务单元标识id
     */
    private String unitId;

    /**
     * 事务切面信息
     */
    private TransactionInfo transactionInfo;

    /**
     * 业务执行器
     */
    private BusinessCallback businessCallback;

    /**
     * 切点方法
     */
    private Method pointMethod;

    /**
     * 事务传播级别
     */
    private DTXPropagation propagation;


}
