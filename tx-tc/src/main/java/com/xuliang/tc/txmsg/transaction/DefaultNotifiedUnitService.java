package com.xuliang.tc.txmsg.transaction;

import com.xuliang.lcn.txmsg.params.NotifyUnitParams;
import com.xuliang.tc.client.TransactionCmd;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxContext;
import com.xuliang.tc.core.template.TransactionCleanTemplate;
import com.xuliang.tc.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;


/**
 * Description: 默认RPC命令业务
 * Date: 2018/12/20
 *
 * @author xuliang
 */
@Slf4j
public class DefaultNotifiedUnitService implements RpcExecuteService {


    private final TransactionCleanTemplate transactionCleanTemplate;

    private TCGlobalContext globalContext;

    public DefaultNotifiedUnitService(TransactionCleanTemplate transactionCleanTemplate, TCGlobalContext globalContext) {
        this.transactionCleanTemplate = transactionCleanTemplate;
        this.globalContext = globalContext;
    }

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {

        NotifyUnitParams notifyUnitParams = transactionCmd.getMsg().loadBean(NotifyUnitParams.class);
        // 保证业务线程执行完毕后执行事物清理操作
        TxContext txContext = globalContext.txContext(notifyUnitParams.getGroupId());
        if (Objects.nonNull(txContext)) {
            synchronized (txContext.getLock()) {
                log.info(
                        " groupId: {} unitId: {} clean transaction cmd waiting for business code finish."
                        , transactionCmd.getGroupId(), notifyUnitParams.getUnitId() );
                txContext.getLock().wait();
            }
        }
        // 事物清理操作
        transactionCleanTemplate.clean(
                notifyUnitParams.getGroupId(),
                notifyUnitParams.getUnitId(),
                notifyUnitParams.getUnitType(),
                notifyUnitParams.getState());
        return true;
    }
}
