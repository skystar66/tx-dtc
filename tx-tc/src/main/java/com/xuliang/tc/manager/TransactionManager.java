package com.xuliang.tc.manager;

import com.xuliang.tc.aspect.DTXInfo;
import com.xuliang.tc.aspect.callback.BusinessCallback;
import com.xuliang.tc.core.LCNServiceExecutor;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * Description:事物管理器
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author xuliang
 */
@Slf4j
@Component
public class TransactionManager {


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

        dtxLocalContext.setUnitId(dtxInfo.getUnitId());//事物单元
        dtxLocalContext.setGroupId(txContext.getGroupId()); //事务组ID
        dtxLocalContext.setTransactionType(dtxInfo.getTransactionType()); //事物操作类型

        log.info("GroupId[{}] dtxLocalContext instance object [{}]",txContext.getGroupId(), dtxLocalContext);

        // 事务参数
        TxTransactionInfo info = new TxTransactionInfo();
        info.setBusinessCallback(business); //回调业务对象
        info.setGroupId(txContext.getGroupId()); //事务组ID
        info.setUnitId(dtxInfo.getUnitId());//事物单元ID
        info.setPointMethod(dtxInfo.getBusinessMethod()); //事物执行业务方法名
        info.setPropagation(dtxInfo.getDtxPropagation()); //事物传播级别
        info.setTransactionInfo(dtxInfo.getTransactionInfo());//事务执行信息
        info.setTransactionType(dtxInfo.getTransactionType()); //事物执行类型
        info.setTransactionStart(txContext.isDtxStart()); //是否是事物发起方

        //LCN事务处理器
        try {
            return lcnServiceExecutor.transactionRunning(info);
        } finally {
            // 线程执行业务完毕清理本地数据
            if (dtxLocalContext.isDestroy()) {
                //通知事务执行完毕
                //此处加锁意义在于等待当前业务代码执行完成，进行通知 检测/notify提交sql 资源做准备
                synchronized (txContext.getLock()) {
                    log.info("LCN 通知检测开始执行检测任务！！！");
                    txContext.getLock().notifyAll();
                }
                // TxContext生命周期是？ 和事务组一样（不与具体模块相关的）
                if (!dtxLocalContext.isInGroup()) {
                    log.info("LCN 销毁当前线程事务上下文执行完毕！！！");
                    tcGlobalContext.destroyTx();
                }

                DTXLocalContext.clear();
                TracingContext.tracing().destroy();
            }
            log.info("<---- TxLcn end ---->");
        }
    }





}
