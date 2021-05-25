package com.xuliang.tc.core.clean.impl;


import com.xuliang.tc.core.connect.proxy.LcnConnectionProxy;
import com.xuliang.tc.core.context.TCGlobalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Date: 2018/12/13
 *
 * @author xuliang
 */
@Component
@Slf4j
public class LcnTransactionCleanServiceImpl implements TransactionCleanService {


    @Autowired
    private TCGlobalContext globalContext;


    @Override
    public void clear(String groupId, int state, String unitId, String unitType) throws Exception {
        LcnConnectionProxy lcnConnectionProxy = globalContext.getLcnConnection(groupId);
        lcnConnectionProxy.notify(state);
    }
}
