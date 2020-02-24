package com.xuliang.lcn.core;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description: 默认事务管理器
 * Date: 19-1-9 下午5:57
 *
 * @author xuliang
 */
@Slf4j
@Component
public class SimpleTransactionManager implements TransactionManager {


    @Override
    public void begin(String groupId) throws Exception {



    }
}
