package com.xuliang.tc.cluster;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.RpcClientInitializer;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.TxManagerHost;
import com.xuliang.lcn.txmsg.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Description:
 * Date: 1/27/19
 *
 * @author xuliang
 * @desc:连接TM集群
 */
@Component
@Slf4j
public class AutoTMClusterEngine {


    @Autowired
    RpcClient rpcClient;

    @Autowired
    RpcClientInitializer rpcClientInitializer;

    public void searchTMCluster(String remoteKey) {
        //搜索TM集群信息，连接TM集群服
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_QUERY_TM_CLUSTER);
        try {
            messageDto = rpcClient.request(remoteKey, messageDto);
            if (MessageUtils.statusOk(messageDto)) {
                HashSet<String> hashSet = messageDto.loadBean(HashSet.class);
                //连接TM集群
                rpcClientInitializer.init(TxManagerHost.parserList(new ArrayList<>(hashSet)), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
