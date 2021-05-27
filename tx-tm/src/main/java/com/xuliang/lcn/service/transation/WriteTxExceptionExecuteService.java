package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Description:写入mysql 异常记录
 * Date: 2018/12/20
 *
 * @author xuliang
 */
@Component("rpc_write-exception")
@Slf4j
public class WriteTxExceptionExecuteService implements RpcExecuteService {


    @Override
    public void execute(RpcCmd transactionCmd) {
        try {
            //todo 增加本地存储记录 mysql

        } catch (Exception ex) {
            log.error("action: rpc_write-exception groupId:{} execute service error. error: {}", transactionCmd.getMsg().getGroupId(), ex);

        }
    }
}
