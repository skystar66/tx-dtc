package com.xuliang.lcn.common.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 事务信息ThreadLocal
 *
 * @author xl
 * @date 2021-05-26
 */
@Slf4j
public class TransactionLocalContextThreadLocal {

    final static ThreadLocal<TransactionLocalContext> threadLocal = new ThreadLocal<>();


        /**
     * 获取或新建一个线程变量。
     *
     * @return 当前线程变量
     */
    public static TransactionLocalContext getNewTransacationContext() {
        if (threadLocal.get() == null) {
            threadLocal.set(new TransactionLocalContext());
        }
        return threadLocal.get();
    }


    public static TransactionLocalContext current() {
        return threadLocal.get();
    }


    public static void push(TransactionLocalContext transactionInfo) {
        threadLocal.set(transactionInfo);
    }

    /**
     * 清理线程变量
     */
    public static void clear() {
        if (threadLocal.get() != null) {
            log.info("clean thread local[{}]: {},thread id : {}",
                    TransactionLocalContextThreadLocal.class.getSimpleName(),
                    current(), Thread.currentThread().getId());
            threadLocal.set(null);
        }
    }

}
