package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.core.manager.LCNTransactionManager;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import com.xuliang.lcn.txmsg.params.CleanGroupParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;


/**
 * Description: 关闭事务组
 * Date: 2018/12/11
 *
 * @author xuliang
 */
@Service("rpc_clean-group")
@Slf4j
public class CleanGroupExecuteService implements RpcExecuteService {


    @Autowired
    FastStorage fastStorage;


    @Autowired
    LCNTransactionManager lcnTransactionManager;

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        try {
            // 解析参数
            CleanGroupParams cleanGroupParams = transactionCmd.getMsg().loadBean(CleanGroupParams.class);
            int commitState = cleanGroupParams.getState();
            String groupId = cleanGroupParams.getGroupId();
            log.info("clean group  groupId:{},state:{}", groupId, commitState);
            //清理事物组
            fastStorage.clearGroup(groupId);
            return RpcResponseState.success;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
