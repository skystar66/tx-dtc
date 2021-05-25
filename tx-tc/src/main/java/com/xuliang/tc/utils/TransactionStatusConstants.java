package com.xuliang.tc.utils;

public class TransactionStatusConstants {


    /**
     * 未知错误
     */
    public static final short UNKNOWN_ERROR = -1;

    /**
     * 通知事务单元失败
     */
    public static final short NOTIFY_UNIT_ERROR = 0;

    /**
     * 询问事务状态失败
     */
    public static final short ASK_ERROR = 1;

    /**
     * 通知事务组失败（TC Starter）
     */
    public static final short NOTIFY_GROUP_ERROR = 2;

    /**
     * TCC 清理事务失败
     */
    public static final short TCC_CLEAN_ERROR = 3;

    /**
     * TXC 撤销日志失败
     */
    public static final Short TXC_UNDO_ERROR = 4;



    /**
     * 近端缓存中，每个缓存写入多久后过期 10分钟
     */
    public static final int WRITE_AFTER_EXPIRE_TIME = 15 * 60 * 1000;

}
