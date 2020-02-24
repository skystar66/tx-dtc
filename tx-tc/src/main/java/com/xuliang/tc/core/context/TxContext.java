package com.xuliang.tc.core.context;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Date: 19-1-16 下午9:23
 *
 * @author xuliang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxContext {

    /**
     * 事务组锁
     */
    private Object lock = new Object();

    /**
     * 事务组ID
     */
    private String groupId;

    /**
     * 分布式事物发起方
     */
    private boolean dtxStart;

    /**
     * 事物创建时间
     */
    private long createTime = System.currentTimeMillis();

    /**
     * 上下文内分布式事务类型
     */
    private Set<String> transactionTypes = new HashSet<>(6);

}
