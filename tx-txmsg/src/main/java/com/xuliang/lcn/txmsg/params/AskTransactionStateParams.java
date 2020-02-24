package com.xuliang.lcn.txmsg.params;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 询问事物状态
 * Date: 2018/12/19
 *
 * @author xuliang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AskTransactionStateParams implements Serializable {


    private String groupId;
    private String unitId;


}
