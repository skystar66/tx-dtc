package com.xuliang.tc.core.commit;

import com.xuliang.lcn.common.enums.TransactionStatus;

/**
 * @author lorne
 * @date 2020/7/1
 * @description
 */
public interface Commitor {

    String type();

    void commit(String groupId, TransactionStatus state);
}
