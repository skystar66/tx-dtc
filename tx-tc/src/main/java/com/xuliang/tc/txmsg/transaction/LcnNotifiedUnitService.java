package com.xuliang.tc.txmsg.transaction;


import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.template.TransactionCleanTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Date: 2018/12/11
 *
 * @author xuliang
 */
@Service("rpc_lcn_notify-unit")
@Slf4j
public class LcnNotifiedUnitService extends DefaultNotifiedUnitService {


    @Autowired
    public LcnNotifiedUnitService(TransactionCleanTemplate transactionCleanTemplate, TCGlobalContext globalContext) {
        super(transactionCleanTemplate, globalContext);
    }
}
