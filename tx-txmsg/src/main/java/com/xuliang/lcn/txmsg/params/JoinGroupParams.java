package com.xuliang.lcn.txmsg.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 加入事务组
 * Date: 2018/12/5
 *
 * @author xuliang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinGroupParams implements Serializable {



    /**事务组id*/
    private String groupId;

    /**
     * 事务单元id
     */
    private String unitId;

    /**
     * 事务单元类型
     */
    private String unitType;

    /**
     * 通讯标识
     */
    private String remoteKey;

    /**
     * 事务状态
     * 0 回滚 1提交
     */
    private int transactionState = 1;


}
