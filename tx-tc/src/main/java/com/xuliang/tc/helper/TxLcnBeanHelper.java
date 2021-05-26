package com.xuliang.tc.helper;


import com.xuliang.lcn.common.enums.TransactionPropagationState;
import com.xuliang.lcn.txmsg.enums.LCNCmdType;
import com.xuliang.tc.core.TransactionControl;
import com.xuliang.tc.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Description: BeanName 获取工具类
 * Date: 2018/12/10
 *
 * @author xl
 */
@Component
@Slf4j
public class TxLcnBeanHelper {


    @Autowired
    private ApplicationContext spring;


    /**
     * message bean 名称格式
     * rpc_%s_%s
     * message:前缀 %s:事务类型（lcn,tcc,txc） %s:事务业务(commit,rollback)
     */
    private static final String RPC_BEAN_NAME_FORMAT = "rpc_%s_%s";

    /**
     * LCNLocalControl bean 名称格式
     * control_%s_%s
     * transaction:前缀 %s:事务类型（lcn,tcc,txc） %s:事务状态(create,join)
     */
    private static final String CONTROL_BEAN_NAME_FORMAT = "control_%s_%s";


    /**
     * 根据 事务类型。业务动作指令 获取相应serviceImpl
     */
    public RpcExecuteService loadRpcExecuteService(String transactionType, LCNCmdType cmdType) {
        return loadRpcExecuteService(getRpcBeanName(transactionType, cmdType));
    }

    private RpcExecuteService loadRpcExecuteService(String rpcBeanName) {
        return spring.getBean(rpcBeanName, RpcExecuteService.class);
    }

    /**
     * 根据事务类型 业务指令 获取rpcBeanName
     */
    private String getRpcBeanName(String transactionType, LCNCmdType cmdType) {
        if (!StringUtils.isEmpty(transactionType)) {
            String name = String.format(RPC_BEAN_NAME_FORMAT, transactionType, cmdType.getCode());
            log.info("getRpcBeanName->{}", name);
            return name;
        } else {
            String name = String.format(RPC_BEAN_NAME_FORMAT.replaceFirst("_%s", ""), cmdType.getCode());
            log.info("getRpcBeanName->{}", name);
            return name;

        }
    }

    /**
     * 获取分布式事务控制器
     */
    public TransactionControl loadDTXLocalControl(String transactionType, TransactionPropagationState lcnTransactionState) {
        return loadDTXLocalControl(getControlBeanName(transactionType, lcnTransactionState));
    }


    private String getControlBeanName(String transactionType, TransactionPropagationState lcnTransactionState) {
        return String.format(CONTROL_BEAN_NAME_FORMAT, transactionType, lcnTransactionState.getCode());
    }


    private TransactionControl loadDTXLocalControl(String beanName) {
        return spring.getBean(beanName, TransactionControl.class);
    }


}
