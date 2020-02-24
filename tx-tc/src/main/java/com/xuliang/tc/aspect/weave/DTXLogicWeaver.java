package com.xuliang.tc.aspect.weave;


import com.xuliang.tc.aspect.DTXInfo;
import com.xuliang.tc.core.LCNServiceExecutor;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author xuliang
 */
@Component
@Slf4j
public class DTXLogicWeaver {


    @Autowired
    TCGlobalContext tcGlobalContext;

    @Autowired
    LCNServiceExecutor lcnServiceExecutor;

    /**
     * 执行事务
     */
    public Object runTransaction(DTXInfo dtxInfo, BusinessCallback business) throws Throwable {

        log.info("<----- TxLcn Start ----->");

        //获取当前线程事物上下文对象
        DTXLocalContext dtxLocalContext = DTXLocalContext.getOrNew();
        // ---------- 保证每个模块在一个DTX下只会有一个TxContext ---------- //

        // 没有的开启本地事务上下文
        TxContext txContext = tcGlobalContext.startTx();

//        // 本地事务调用
//        if (Objects.nonNull(dtxLocalContext.getGroupId())) {
//            log.info(">>>>>>>>> 被调用方不能销毁");
//            dtxLocalContext.setDestroy(false);
//        }

        dtxLocalContext.setUnitId(dtxInfo.getUnitId());
        dtxLocalContext.setGroupId(txContext.getGroupId());
        dtxLocalContext.setTransactionType(dtxInfo.getTransactionType());

        log.info("GroupId[{}] dtxLocalContext instance object [{}]", dtxLocalContext);

        // 事务参数
        TxTransactionInfo info = new TxTransactionInfo();
        info.setBusinessCallback(business);
        info.setGroupId(txContext.getGroupId());
        info.setUnitId(dtxInfo.getUnitId());
        info.setPointMethod(dtxInfo.getBusinessMethod());
        info.setPropagation(dtxInfo.getDtxPropagation());
        info.setTransactionInfo(dtxInfo.getTransactionInfo());
        info.setTransactionType(dtxInfo.getTransactionType());
        info.setTransactionStart(txContext.isDtxStart());

        //LCN事务处理器
        try {
            return lcnServiceExecutor.transactionRunning(info);
        } finally {
            // 线程执行业务完毕清理本地数据
            if (dtxLocalContext.isDestroy()) {
                //通知事务执行完毕
                synchronized (txContext.getLock()) {
                    log.info("LCN 通知事务执行完毕！！！");
                    txContext.getLock().notifyAll();
                }
                // TxContext生命周期是？ 和事务组一样（不与具体模块相关的）
                if (!dtxLocalContext.isInGroup()) {
                    log.info("LCN 销毁事务执行完毕！！！");
                    tcGlobalContext.destroyTx();
                }

                DTXLocalContext.clear();
                TracingContext.tracing().destroy();
            }
            log.info("<---- TxLcn end ---->");
        }
    }


}
