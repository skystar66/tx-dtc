package com.xuliang.lcn.core.manager;


import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.core.storage.TransactionUnit;
import com.xuliang.lcn.server.MessageCreator;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.params.NotifyUnitParams;
import com.xuliang.lcn.txmsg.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * Description: redis 快速缓存 Manager cache
 * Date: 19-1-21 下午2:53
 *
 * @author xuliang
 */
@Component
@Slf4j
public class LCNTransactionManager {


    @Autowired
    FastStorage fastStorage;

    @Autowired
    RpcClient rpcClient;


    /**
     * 提交分布式事务。出错会记录异常记录
     *
     * @param groupId
     */
    public void commit(String groupId) throws Exception {

        notifyTransaction(groupId, 1);

    }


    /**
     * 回滚分布式事务。出错会记录异常记录
     *
     * @param groupId transaction
     */
    public void rollback(String groupId) throws Exception {
        notifyTransaction(groupId, 0);


    }

    /**
     * 关闭分布式事务。出错会记录异常记录
     *
     * @param groupId groupId
     */
    public void close(String groupId) {
        try {
            fastStorage.clearGroup(groupId);
        } catch (Exception e) {
            log.error("error：{}", e);
        }
    }


    /**
     * 开始事务处理
     *
     * @param groupId groupId
     */
    private void notifyTransaction(String groupId, int transactionState) throws Exception {
        List<TransactionUnit> transactionUnits = fastStorage.findTransactionUnitsFromGroup(groupId);
        log.info("group[{}]'s transaction units: {}", groupId, transactionUnits);
        for (TransactionUnit transactionUnit : transactionUnits) {
            NotifyUnitParams notifyUnitParams = new NotifyUnitParams();
            notifyUnitParams.setGroupId(groupId);
            notifyUnitParams.setState(transactionState);
            notifyUnitParams.setUnitId(transactionUnit.getUnitId());
            notifyUnitParams.setUnitType(transactionUnit.getUnitType());
            log.info(groupId, notifyUnitParams.getUnitId(), "notify TM's unit: {}",
                    transactionUnit.getUnitId());
            try {
                //获取远程标识
//                String remoteKey = rpcClient.loadRemoteKey();

                List<String> modChannelKeys = rpcClient.remoteKeys(transactionUnit.getModId());
                if (modChannelKeys.isEmpty()) {
                    // record exception
                    throw new Exception("offline mod.");
                }
                log.info("remote channel key : {}", modChannelKeys);
                MessageDto respMsg =
                        rpcClient.request(modChannelKeys.get(0), MessageCreator.notifyUnit(notifyUnitParams));
                if (!MessageUtils.statusOk(respMsg)) {
                    // 提交/回滚失败的消息处理 记录在本地数据库中 admin页面展示
                }
            } catch (Exception e) {
                // 记录在本地数据库中
            } finally {
                log.info(groupId, notifyUnitParams.getUnitId(), "notify unit over");
            }
        }
    }
}
