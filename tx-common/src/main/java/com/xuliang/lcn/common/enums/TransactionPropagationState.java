package com.xuliang.lcn.common.enums;


/**
 * @author xuliang
 */
public enum TransactionPropagationState {



    /**
     * 创建DTX
     */
    CREATE("create"),

    /**
     * 加入DTX
     */
    JOIN("join"),

    /**
     * notify
     */
    NOTIFY("notify"),

    /**
     * 静默加入DTX
     */
    SILENT_JOIN("default"),

    /**
     * 不参与DTX
     */
    NON("non");


    private String code;


    TransactionPropagationState(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }

    public boolean isIgnored() {
        return this.equals(NON);
    }


}
