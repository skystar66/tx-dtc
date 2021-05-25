package com.xuliang.tc.core.context;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 分布式事务远程调用控制对象
 * ！！！不推荐用户业务使用，API变更性大，使用不当有可能造成事务流程出错 ！！！
 * <p>
 * Created by xuliang on 2017/6/5.
 */
@Data
@Slf4j
public class DTXLocalContext {

    private final static ThreadLocal<DTXLocalContext> currentLocal
            = new ThreadLocal<>();

    /**
     * 获取或新建一个线程变量。
     *
     * @return 当前线程变量
     */
    public static DTXLocalContext getOrNew() {
        if (currentLocal.get() == null) {
            currentLocal.set(new DTXLocalContext());
        }
        return currentLocal.get();
    }


    /**
     * 获取当前线程变量。不推荐用此方法，会产生NullPointerException
     *
     * @return 当前线程变量
     */
    public static DTXLocalContext cur() {
        return currentLocal.get();
    }

    /**
     * 清理线程变量
     */
    public static void clear() {
        if (currentLocal.get() != null) {
            log.info("clean thread local[{}]: {},thread id : {}", DTXLocalContext.class.getSimpleName(),
                    cur(), Thread.currentThread().getId());
            currentLocal.set(null);
        }
    }

    /**
     * 事务组id
     */
    private String groupId;


    /**
     * 事务单元id
     */
    private String unitId;

    /**
     * 事务类型
     */
    private String transactionType;


    /**
     * 是否需要销毁。什么时候需要？一个请求下来，
     * 这个模块有两个Unit被执行，那么被调方是不能销毁的，
     * 只能有上层调用方销毁
     */
    private boolean destroy = true;


    /**
     * 同事务组标识
     */
    private boolean inGroup;


    /**
     * 是否代理资源
     */
    private boolean proxy;

    /**
     * 代理资源临时值
     */
    private boolean proxyTmp;

    /**
     * 系统分布式事务状态
     */
    private int sysTransactionState = 1;

    /**
     * 设置代理资源
     */
    public static void makeProxy() {
        if (cur() != null) {
            System.out.println(cur().proxy + "--1--" + cur().proxyTmp);
            cur().proxyTmp = cur().proxy;
            System.out.println(cur().proxy + "--2--" + cur().proxyTmp);
            cur().proxy = true;
            System.out.println(cur().proxy + "--3--" + cur().proxyTmp);
        }
    }

    /**
     * 事务状态
     *
     * @param userDtxState state
     * @return 1 commit 0 rollback
     */
    public static int transactionState(int userDtxState) {
        DTXLocalContext dtxLocalContext = Objects.requireNonNull(currentLocal.get(), "DTX can't be null.");
        return userDtxState == 1 ? dtxLocalContext.sysTransactionState : userDtxState;
    }


}
