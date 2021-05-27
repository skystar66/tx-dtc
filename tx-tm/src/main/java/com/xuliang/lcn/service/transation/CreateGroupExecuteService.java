package com.xuliang.lcn.service.transation;


import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    MessageSender messageSender;


    @Override
    public void execute(RpcCmd transactionCmd) {
        try {
            fastStorage.initGroup(transactionCmd.getMsg().getGroupId());
            messageSender.senderSuccess(transactionCmd, RpcResponseState.success);
        } catch (Exception e) {
            log.error("action: rpc_create-group groupId:{} execute service error. error: {}", transactionCmd.getMsg().getGroupId(),
                    e);
            messageSender.senderFail(transactionCmd, e);
        }
    }
}
