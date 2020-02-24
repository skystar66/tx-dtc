package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * Description:
 * Date: 2018/12/20
 *
 * @author xuliang
 */
@Component("rpc_write-exception")
@Slf4j
public class WriteTxExceptionExecuteService implements RpcExecuteService {


    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        //增加本地存储记录 mysql
        return null;
    }
}
