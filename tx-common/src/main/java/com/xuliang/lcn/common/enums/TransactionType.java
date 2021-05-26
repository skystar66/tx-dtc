package com.xuliang.lcn.common.enums;

/**
 * @author xuliang
 * @desc:事务类型
 */
public enum TransactionType {


    LCN("lcn"),;

    private String type;


    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
