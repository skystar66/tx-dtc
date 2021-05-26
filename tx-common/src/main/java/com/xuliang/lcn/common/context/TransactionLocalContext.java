package com.xuliang.lcn.common.context;


import com.xuliang.lcn.common.enums.TransactionPropagation;
import com.xuliang.lcn.common.enums.TransactionPropagationState;
import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.common.enums.TransactionType;
import com.xuliang.lcn.common.util.SnowflakeIdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 分布式事务远程调用控制对象
 * ！！！不推荐用户业务使用，API变更性大，使用不当有可能造成事务流程出错 ！！！
 * <p>
 * Created by xuliang on 2017/6/5.
 */
@Data
@Slf4j
public class TransactionLocalContext {

    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 事务单元id
     */
    private String unitId;

    /**
     * 事物发起方
     */
    private boolean isStart;

    /**
     * 事务类型
     */
    private String transactionType;

    /**
     * 同事务组标识
     */
    private boolean inGroup;


    /**
     * 系统分布式事务状态
     */
    private TransactionStatus sysTransactionState = TransactionStatus.SUCCESS;

    /**
     * 事务阶段
     */
    private TransactionPropagationState transactionState;



    /**
     * 事务传播级别
     */
    private TransactionPropagation propagation;


    /**
     * 加入事物
     */
    public TransactionLocalContext(String groupId) {
        this.groupId = groupId;
        this.unitId = String.valueOf(SnowflakeIdWorker.getInstance().nextId());
        this.transactionState = TransactionPropagationState.JOIN;
        this.isStart = false;
        TransactionLocalContextThreadLocal.push(this);
    }


    /**
     * 创建事物
     */
    public TransactionLocalContext() {
        this.groupId = String.valueOf(SnowflakeIdWorker.getInstance().nextId());
        this.transactionState = TransactionPropagationState.CREATE;
        this.isStart = true;//事物发起方
        TransactionLocalContextThreadLocal.push(this);
    }

    /**
     * 需要SQL代理的模式判断
     * LCN
     *
     * @return
     */
    public boolean hasSqlProxy() {
        if (transactionType.equals(TransactionType.LCN.getType())) {
            return true;
        }
        return false;
    }


    public boolean isState(TransactionPropagationState state){
        if(transactionState==null){
            return false;
        }
        return transactionState.equals(state);
    }

}
