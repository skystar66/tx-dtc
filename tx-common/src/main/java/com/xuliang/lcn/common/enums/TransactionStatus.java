package com.xuliang.lcn.common.enums;

/**
 * @author xuliang
 * @desc:事务状态
 */
public enum TransactionStatus {


    SUCCESS(1),
    FAIL(0),
    ;

    private int status;

    TransactionStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static TransactionStatus getTransactionStatus(int status) {
        for (TransactionStatus transactionStatus : TransactionStatus.values()) {
            if (status == transactionStatus.getStatus()) {
                return transactionStatus;
            }
        }
        return null;
    }



}
