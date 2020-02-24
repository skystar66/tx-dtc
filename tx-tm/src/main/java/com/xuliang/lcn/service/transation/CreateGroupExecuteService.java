package com.xuliang.lcn.service.transation;


import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Description: 创建事物组
 * Date: 2018/12/11
 *
 * @author xuliang
 */
@Service("rpc_create-group")
@Slf4j
public class CreateGroupExecuteService implements RpcExecuteService {


    @Autowired
    private FastStorage fastStorage;


    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        try {
            fastStorage.initGroup(transactionCmd.getGroupId());
        } catch (Exception e) {
            throw new Exception(e);
        }
        log.info(transactionCmd.getGroupId(), null, "created group");
        return null;
    }
}
