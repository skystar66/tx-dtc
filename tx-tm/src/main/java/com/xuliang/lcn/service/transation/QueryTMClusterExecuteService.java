package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.cluster.TMProperties;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;


/**
 * Description:
 * Date: 19-1-25 上午11:05
 *
 * @author xuliang
 */
@Component("rpc_query-tm-cluster")
@Slf4j
public class QueryTMClusterExecuteService implements RpcExecuteService {

    @Autowired
    private FastStorage fastStorage;

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        try {
            HashSet<String> tmSet = new HashSet<>();
            for (TMProperties props : fastStorage.findTMProperties()) {
                tmSet.add(props.getHost() + ":" + props.getTransactionPort());
            }
            log.info("Query TM cluster. {}", tmSet);
            return tmSet;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
