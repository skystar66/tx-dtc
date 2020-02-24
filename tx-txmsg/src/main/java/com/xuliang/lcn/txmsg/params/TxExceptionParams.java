package com.xuliang.lcn.txmsg.params;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description:
 * Date: 2018/12/20
 *
 * @author xuliang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TxExceptionParams implements Serializable {


    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 事物单元id
     */
    private String unitId;

    /**
     * 异常情况
     */
    private Short registrar;

    /**
     * 事务状态 0 回滚 1提交
     */
    private Integer transactionState;

    /**
     * 备注
     */
    private String remark;


}
