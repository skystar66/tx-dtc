package com.xuliang.tc.core.context;

import com.xuliang.tc.core.connect.proxy.LcnConnectionProxy;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DefaultGlobalContext implements TCGlobalContext {

    @Autowired
    AttachmentCache attachmentCache;

    @Override
    public TxContext startTx() {
        TxContext txContext = new TxContext();
        // 事务发起方判断
        txContext.setDtxStart(!TracingContext.tracing().hasGroup());
        if (txContext.isDtxStart()) {
            log.info("开启事务 groupId：{} ", TracingContext.tracing().groupId());
            TracingContext.tracing().beginTransactionGroup();
        } else {
            log.info("参与事务 groupId：{} ", TracingContext.tracing().groupId());
        }
        txContext.setGroupId(TracingContext.tracing().groupId());
        String txContextKey = txContext.getGroupId() + ".dtx";
        attachmentCache.attach(txContextKey, txContext);
        return txContext;
    }


    @Override
    public void destroyTx() {
        if (!hasTxContext()) {
            throw new IllegalStateException("non TxContext.");
        }
        destroyTx(txContext().getGroupId());
    }


    /**
     * 在用户业务前生成，业务后销毁
     *
     * @param groupId groupId
     */
    @Override
    public void destroyTx(String groupId) {
        attachmentCache.remove(groupId + ".dtx");
        log.debug("Destroy TxContext[{}]", groupId);
    }


    @Override
    public boolean hasTxContext() {
        return TracingContext.tracing().hasGroup() && txContext(TracingContext.tracing().groupId()) != null;
    }

    @Override
    public TxContext txContext() {
        return txContext(TracingContext.tracing().groupId());
    }

    @Override
    public TxContext txContext(String groupId) {
        return attachmentCache.attachment(groupId + ".dtx");
    }


    @Override
    public int dtxState(String groupId) {
        return this.attachmentCache.containsKey(groupId, "rollback-only") ? 0 : 1;
    }

    @Override
    public LcnConnectionProxy getLcnConnection(String groupId) throws Exception {
        if (attachmentCache.containsKey(groupId, LcnConnectionProxy.class.getName())) {
            return attachmentCache.attachment(groupId, LcnConnectionProxy.class.getName());
        }
        return null;
    }

    @Override
    public void setLcnConnection(String groupId, LcnConnectionProxy connectionProxy) {
        attachmentCache.attach(groupId, LcnConnectionProxy.class.getName(), connectionProxy);
    }

    @Override
    public void clearGroup(String groupId) {
        attachmentCache.removeAll(groupId);
    }


    @Override
    public void setRollbackOnly(String groupId) {
        this.attachmentCache.attach(groupId, "rollback-only", true);
    }
}
