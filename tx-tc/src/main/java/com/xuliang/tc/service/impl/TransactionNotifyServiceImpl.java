package com.xuliang.tc.service.impl;

import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.txmsg.params.NotifyUnitParams;
import com.xuliang.tc.core.TransactionMsgSenger;
import com.xuliang.tc.core.cache.TcCache;
import com.xuliang.tc.core.checking.TransactionChecking;
import com.xuliang.tc.core.strategy.TransactionCommitorStrategy;
import com.xuliang.tc.txmsg.TransactionCmd;
import com.xuliang.tc.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Description:事物通知单元
 * Date: 2018/12/11
 *
 * @author xuliang
 */
@Service("rpc_lcn_notify-unit")
@Slf4j
public class TransactionNotifyServiceImpl implements RpcExecuteService {


    @Autowired
    private TransactionCommitorStrategy transactionCommitorStrategy;

    @Autowired
    private TransactionChecking transactionChecking;

    @Autowired
    private TransactionMsgSenger transactionMsgSenger;

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {

        NotifyUnitParams notifyUnitParams = transactionCmd.getMsg().loadBean(NotifyUnitParams.class);
        //事物执行操作
        transactionCommitorStrategy.commit(notifyUnitParams.getGroupId(),
                notifyUnitParams.getUnitType(),
                TransactionStatus.getTransactionStatus(notifyUnitParams.getState()));
        //清理异常检测
        transactionChecking.stopDelayChecking(notifyUnitParams.getGroupId(),notifyUnitParams.getUnitId());
        //清理连接缓存
        TcCache.getInstance().cleanTransactionConnection(notifyUnitParams.getGroupId());
        //清理事物组
        transactionMsgSenger.cleanGroup(notifyUnitParams.getGroupId(),notifyUnitParams.getState());
        return true;
    }
}
