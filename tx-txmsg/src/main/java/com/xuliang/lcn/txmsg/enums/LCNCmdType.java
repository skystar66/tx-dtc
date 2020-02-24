package com.xuliang.lcn.txmsg.enums;


import com.xuliang.lcn.txmsg.MessageConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xuliang
 */
@Slf4j
public enum LCNCmdType {


    /**
     * 初始化客户端
     * 简写 ic
     */
    initClient("init-client", MessageConstants.ACTION_INIT_CLIENT),

    /**
     * 查询tm 集群
     */
    queryTMCluster("query-tm-cluster", MessageConstants.ACTION_QUERY_TM_CLUSTER),



    /**
     * 通知事务单元
     */
    notifyUnit("notify-unit", MessageConstants.ACTION_NOTIFY_UNIT),

    /**
     * 创建事务组
     * <p>
     * 简写 cg
     */
    createGroup("create-group", MessageConstants.ACTION_CREATE_GROUP),

    /**
     * 加入事务组
     * <p>
     * 简写 cg
     */
    joinGroup("join-group", MessageConstants.ACTION_JOIN_GROUP),

    /**
     * 通知事务组
     * 简写 clg
     */
    notifyGroup("notify-group", MessageConstants.ACTION_NOTIFY_GROUP),


    /**
     * 响应事务状态
     * 间写 ats
     */
    askTransactionState("ask-transaction-state", MessageConstants.ACTION_ASK_TRANSACTION_STATE),


    /**
     * 记录补偿
     * 简写 wc
     */
    writeCompensation("write-exception", MessageConstants.ACTION_WRITE_EXCEPTION),




    ;


    private String code;

    private String name;

    LCNCmdType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static LCNCmdType parserCmd(String cmd) {
        log.info("parsed txmsg cmd: {}", cmd);
        switch (cmd) {
            case MessageConstants.ACTION_CREATE_GROUP:
                return createGroup;
            case MessageConstants.ACTION_NOTIFY_GROUP:
                return notifyGroup;
            case MessageConstants.ACTION_NOTIFY_UNIT:
                return notifyUnit;
            case MessageConstants.ACTION_JOIN_GROUP:
                return joinGroup;
            case MessageConstants.ACTION_ASK_TRANSACTION_STATE:
                return askTransactionState;
            case MessageConstants.ACTION_WRITE_EXCEPTION:
                return writeCompensation;
            case MessageConstants.ACTION_INIT_CLIENT:
                return initClient;
            case MessageConstants.ACTION_QUERY_TM_CLUSTER:
                return queryTMCluster;

            default:
                throw new IllegalStateException("unsupported cmd.");
        }
    }

}
