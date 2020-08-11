package com.xuliang.lcn.txmsg;


/**
 * Description: cmd 指令
 * Date: 2018/12/6
 *
 * @author xuliang
 */

public class MessageConstants {

    /**
     * 心态检测
     */
    public static final String ACTION_HEART_CHECK = "heartCheck";

    /**
     * 事务通知
     */
    public static final String ACTION_NOTIFY_UNIT = "notifyUnit";


    /**
     * 初始化客户端
     */
    public static final String ACTION_INIT_CLIENT = "init";

    /**
     * 查询TM Cluster 集群
     */
    public static final String ACTION_QUERY_TM_CLUSTER = "qtmc";

    /**
     * 发起请求状态
     */
    public static final int STATE_REQUEST = 100;

    /**
     * 响应成功状态
     */
    public static final int STATE_OK = 200;

    /**
     * 响应异常状态
     */
    public static final int STATE_EXCEPTION = 500;

    /**
     * 创建事务组
     */
    public static final String ACTION_CREATE_GROUP = "createGroup";
    /**
     * 关闭事务组
     */
    public static final String ACTION_NOTIFY_GROUP = "notifyGroup";

    /**
     * 加入事务组
     */
    public static final String ACTION_JOIN_GROUP = "joinGroup";

    /**
     * 询问事务状态
     */
    public static final String ACTION_ASK_TRANSACTION_STATE = "askTransactionState";

    /**
     * 写异常记录
     */
    public static final String ACTION_WRITE_EXCEPTION = "writeException";


    /**通知建立连接*/
    public static final String ACTION_NEW_TXMANAGER = "ntm";

}
