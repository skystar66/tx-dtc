package com.xuliang.tc.aspect.interceptor;


import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tc.aspect.DTXInfo;
import com.xuliang.tc.aspect.enums.DTXPropagation;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;
import java.util.Properties;

/**
 * Description:
 * Company: CodingApi
 * Date: 2019/1/13
 *
 * @author xuliang
 */
@Slf4j
public class InterceptorInvocationUtils {

    static DTXInfo load(MethodInvocation invocation, Properties transactionAttributes) {

        //默认值
        String transactionType = "lcn";
        DTXPropagation dtxPropagation = null;
        //优先获取配置的信息
        if (transactionAttributes != null) {
            transactionType = transactionAttributes.getProperty(Transactions.DTX_TYPE);
            dtxPropagation = DTXPropagation.parser(transactionAttributes.getProperty(Transactions.DTX_PROPAGATION));
        } else {
            LcnTransaction lcnTransaction = invocation.getMethod().getAnnotation(LcnTransaction.class);
            log.info("获取注解的信息 lcnTransaction LcnTransaction : {}", lcnTransaction);
            if (Objects.nonNull(lcnTransaction)) {
                dtxPropagation = lcnTransaction.propagation();
            } else {

                log.info("请正确配置事物类型LCN！！！");
                return null;
            }
        }
        DTXInfo dtxInfo = DTXInfo.getFromCache(invocation);
        dtxInfo.setTransactionType(transactionType);
        dtxInfo.setDtxPropagation(dtxPropagation);
        return dtxInfo;
    }


}
