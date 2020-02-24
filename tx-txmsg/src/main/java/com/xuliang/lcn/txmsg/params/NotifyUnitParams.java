package com.xuliang.lcn.txmsg.params;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description:
 * Date: 2018/12/5
 *
 * @author xuliang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotifyUnitParams implements Serializable {


    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 事物单元id
     */
    private String unitId;

    /**
     * 事物单元类型
     */
    private String unitType;

    /**
     * 事物状态
     */
    private int state;


}
