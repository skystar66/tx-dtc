package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.cluster.TMProperties;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;


/**
 * Description:查询TM集群，//todo 以后全部替换zk
 * Date: 19-1-25 上午11:05
 *
 * @author xuliang
 */
@Component("rpc_query-tm-cluster")
@Slf4j
public class QueryTMClusterExecuteService implements RpcExecuteService {

    @Autowired
    private FastStorage fastStorage;


    @Autowired
    MessageSender messageSender;

    @Override
    public void execute(RpcCmd transactionCmd) {
        try {
            HashSet<String> tmSet = new HashSet<>();
            for (TMProperties props : fastStorage.findTMProperties()) {
                tmSet.add(props.getHost() + ":" + props.getTransactionPort());
            }
            log.info("Query TM cluster. {}", tmSet);
            messageSender.senderSuccess(transactionCmd, tmSet);
        } catch (Exception e) {
            log.error("action: rpc_query-tm-cluster groupId:{} execute service error. error: {}",transactionCmd.getMsg().getGroupId(), e);
            messageSender.senderFail(transactionCmd, e);
        }
    }
}
