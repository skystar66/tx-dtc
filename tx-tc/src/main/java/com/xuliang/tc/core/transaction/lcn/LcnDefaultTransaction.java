package com.xuliang.tc.core.transaction.lcn;

import com.xuliang.tc.core.LCNLocalControl;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Date: 2018/12/3
 *
 * @author xuliang
 */
@Component("control_lcn_default")
@Slf4j
public class LcnDefaultTransaction implements LCNLocalControl {

    @Override
    public void preBusinessCode(TxTransactionInfo info) throws Exception {

        DTXLocalContext.makeProxy();
    }
}
