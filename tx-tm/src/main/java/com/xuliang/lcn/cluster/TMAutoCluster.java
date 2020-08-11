package com.xuliang.lcn.cluster;


import com.xuliang.lcn.common.runner.TxLcnInitializer;
import com.xuliang.lcn.common.runner.TxLcnRunnerOrders;
import com.xuliang.lcn.common.util.ApplicationInformation;
import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.txmsg.params.NotifyConnectParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * Date: 1/24/20
 *
 * @author xuliang
 * @desc:服务端：自动集群连接
 */
@Component
@Slf4j
public class TMAutoCluster implements TxLcnInitializer {


    @Autowired
    FastStorage fastStorage;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TxManagerConfig txManagerConfig;


    private static final String MANAGER_REFRESH_URL = "http://%s:%s/manager/refresh";

    @Autowired
    private ServerProperties serverProperties;


    @Override
    public void init() throws Exception {
        //通知 tc client 连接当前tm 服务
        List<TMProperties> tmList = fastStorage.findTMProperties().stream().filter(tmProperties ->
                !tmProperties.getHost().equals(txManagerConfig.getHost()) ||
                        !tmProperties.getTransactionPort().equals(txManagerConfig.getPort()))
                .collect(Collectors.toList());
        log.info("TM Server Info List ： {}", tmList);
        for (TMProperties properties : tmList) {
            NotifyConnectParams notifyConnectParams = new NotifyConnectParams();
            notifyConnectParams.setHost(txManagerConfig.getHost());
            notifyConnectParams.setPort(txManagerConfig.getPort());
            //构造 tcClient url
            String url = String.format(MANAGER_REFRESH_URL, properties.getHost(),
                    properties.getHttpPort());
            try {
                ResponseEntity<Boolean> res = restTemplate.postForEntity(url, notifyConnectParams, Boolean.class);
                if (res.getStatusCode().equals(HttpStatus.OK) || res.getStatusCode().is5xxServerError()) {
                    log.info("manager auto refresh res->{}", res);
                    break;
                } else {
                    fastStorage.removeTMProperties(properties.getHost(), properties.getTransactionPort());
                }

            } catch (Exception e) {
                log.error("manager auto refresh error: {}", e.getMessage());
                //check exception then remove.
                if (e instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) e;
                    if (resourceAccessException.getCause() != null && resourceAccessException.getCause() instanceof ConnectException) {
                        //can't access .
                        fastStorage.removeTMProperties(properties.getHost(), properties.getTransactionPort());
                    }
                }
            }
        }
        //快速保存tm 信息
        if (StringUtils.hasText(txManagerConfig.getHost())) {
            TMProperties tmProperties = new TMProperties();
            tmProperties.setHttpPort(ApplicationInformation.serverPort(serverProperties));
            tmProperties.setHost(txManagerConfig.getHost());
            tmProperties.setTransactionPort(txManagerConfig.getPort());
            fastStorage.saveTMProperties(tmProperties);
        }
        log.info("TMAutoCluster Started Success！！！");
    }


    @Override
    public void destroy() throws Exception {
        fastStorage.removeTMProperties(txManagerConfig.getHost(), txManagerConfig.getPort());

    }


    @Override
    public int order() {
        return TxLcnRunnerOrders.MIN;
    }
}
