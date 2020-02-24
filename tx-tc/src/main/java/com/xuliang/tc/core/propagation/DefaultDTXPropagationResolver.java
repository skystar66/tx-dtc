package com.xuliang.tc.core.propagation;

import com.xuliang.tc.aspect.enums.DTXPropagation;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tc.core.enums.DTXPropagationState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description: 事务传播逻辑处理
 * Date: 2018/12/5
 *
 * @author xuliang
 */
@Slf4j
@Component
public class DefaultDTXPropagationResolver implements DTXPropagationResolver {

    @Override
    public DTXPropagationState resolvePropagationState(TxTransactionInfo txTransactionInfo){
        // 本地已在DTX，根据事务传播，静默加入
        if (DTXLocalContext.cur().isInGroup()) {
            log.info("SILENT_JOIN group!");
            return DTXPropagationState.SILENT_JOIN;
        }

        // 发起方之前没有事务
        if (txTransactionInfo.isTransactionStart()) {
            // 根据事务传播，对于 SUPPORTS 不参与事务
            if (DTXPropagation.SUPPORTS.equals(txTransactionInfo.getPropagation())) {
                return DTXPropagationState.NON;
            }
            // 根据事务传播，创建事务
            return DTXPropagationState.CREATE;
        }

        // 已经存在DTX，根据事务传播，加入
        return DTXPropagationState.JOIN;
    }
}
