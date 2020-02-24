package com.xuliang.lcn.core.storage;


import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 * Date: 19-1-21 下午3:13
 *
 * @author xuliang
 */
@Data
public class TransactionUnit  implements Serializable {


    /**
     * 事务单元所在模块标识
     */
    private String modId;

    /**
     * 事务类型
     */
    private String unitType;

    /**
     * 相关业务方法签名
     */
    private String unitId;


}
