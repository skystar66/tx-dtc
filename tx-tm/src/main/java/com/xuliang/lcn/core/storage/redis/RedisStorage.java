package com.xuliang.lcn.core.storage.redis;

import com.xuliang.lcn.cluster.TMProperties;
import com.xuliang.lcn.common.util.ApplicationInformation;
import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.core.storage.TransactionUnit;
import com.xuliang.lcn.utils.RedisKeyUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xuliang.lcn.utils.RedisKeyUtils.REDIS_TOKEN_PREFIX;

@Component
@Data
@Slf4j
public class RedisStorage implements FastStorage {


    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TxManagerConfig managerConfig;


    /**
     * find all Manager
     * 查找所有的 tm 端 配置
     *
     * @return list
     * @throws FastStorageException fastStorageException
     */
    @Override
    public List<TMProperties> findTMProperties() throws Exception {
        List<TMProperties> tmPropertiesList = stringRedisTemplate.opsForHash().entries(RedisKeyUtils.REDIS_TM_LIST).entrySet().stream()
                .map(entry -> {
                    String[] args = ApplicationInformation.splitAddress(entry.getKey().toString());
                    TMProperties tmProperties = new TMProperties();
                    tmProperties.setHost(args[0]);
                    tmProperties.setTransactionPort(Integer.valueOf(args[1]));
                    tmProperties.setHttpPort(Integer.parseInt(entry.getValue().toString()));
                    return tmProperties;
                }).collect(Collectors.toList());

        return tmPropertiesList;
    }


    /**
     * delete Manager address
     * 删除tm服务
     *
     * @param host            host
     * @param transactionPort transactionPort
     * @throws FastStorageException fastStorageException
     */

    @Override
    public void removeTMProperties(String host, int transactionPort) throws Exception {
        redisTemplate.opsForHash().delete(RedisKeyUtils.REDIS_TM_LIST, host + ":" + transactionPort);
        log.info("removed TM {}:{}", host, transactionPort);

    }

    /**
     * save Manager address is ip:port
     * 保存tm 服务信息
     *
     * @param tmProperties ip:port
     * @throws FastStorageException fastStorageException
     */
    @Override
    public void saveTMProperties(TMProperties tmProperties) throws Exception {
        stringRedisTemplate.opsForHash().put(RedisKeyUtils.REDIS_TM_LIST, tmProperties.getHost() + ":" + tmProperties.getTransactionPort(),
                String.valueOf(tmProperties.getHttpPort()));
    }

    /**
     * init DTX group.
     * 初始化事物组信息
     * note: group info should clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @throws FastStorageException fastStorageException
     */
    @Override
    public void initGroup(String groupId) throws Exception {
        redisTemplate.opsForHash().put(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId, "root", "");
        redisTemplate.expire(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId, managerConfig.getDtxTime() + 10000, TimeUnit.MILLISECONDS);

    }


    /**
     * join DTX group.
     * 加入当前事务组
     * note: group info should clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @throws FastStorageException fastStorageException
     */

    @Override
    public void saveTransactionUnitToGroup(String groupId, TransactionUnit transactionUnit) {
        if (Optional.ofNullable(redisTemplate.hasKey(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId)).orElse(false)) {
            redisTemplate.opsForHash().put(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId, transactionUnit.getUnitId(), transactionUnit);
            return;
        }
    }


    /**
     * save DTX state
     * 保存事物组状态
     * note: transaction state must clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @param state   status 1 commit 0 rollback
     * @throws FastStorageException fastStorageException
     */

    @Override
    public void saveTransactionState(String groupId, int state) throws Exception {
        redisTemplate.opsForValue().set(RedisKeyUtils.REDIS_GROUP_STATE + groupId, String.valueOf(state));
        redisTemplate.expire(RedisKeyUtils.REDIS_GROUP_STATE + groupId, managerConfig.getDtxTime() + 10000, TimeUnit.MILLISECONDS);

    }


    /**
     * get DTC state
     * 获取事物状态
     *
     * @param groupId groupId
     * @return int
     * @throws FastStorageException fastStorageException
     */

    @Override
    public int getTransactionState(String groupId) throws Exception {
        Object state = redisTemplate.opsForValue().get(RedisKeyUtils.REDIS_GROUP_STATE + groupId);
        if (Objects.isNull(state)) {
            return -1;
        }

        try {
            return Integer.valueOf(state.toString());
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * get group all unit
     * 获取该事务组下的所有事物单元
     *
     * @param groupId groupId
     * @return list
     * @throws FastStorageException fastStorageException
     */
    @Override
    public List<TransactionUnit> findTransactionUnitsFromGroup(String groupId) throws Exception {
        Map<Object, Object> units = redisTemplate.opsForHash()
                .entries(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId);
        return units.entrySet().stream()
                .filter(objectObjectEntry -> !objectObjectEntry.getKey().equals("root"))
                .map(objectObjectEntry -> (TransactionUnit) objectObjectEntry.getValue()).collect(Collectors.toList());

    }


    /**
     * clear group
     * 清理事务组
     *
     * @param groupId groupId
     * @throws Exception fastStorageException
     */
    @Override
    public void clearGroup(String groupId) throws Exception {
        log.debug("remove group:{} from redis.", groupId);
        redisTemplate.delete(RedisKeyUtils.REDIS_GROUP_PREFIX + groupId);
    }


    @Override
    public void saveToken(String token) {
        Objects.requireNonNull(token);
        redisTemplate.opsForList().leftPush(REDIS_TOKEN_PREFIX, token);
        redisTemplate.expire(REDIS_TOKEN_PREFIX, 20, TimeUnit.MINUTES);

        Long size = redisTemplate.opsForList().size(REDIS_TOKEN_PREFIX);
        if (Objects.nonNull(size) && size > 3) {
            redisTemplate.opsForList().rightPop(REDIS_TOKEN_PREFIX);
        }
    }

    @Override
    public List<String> findTokens() {
        Long size = redisTemplate.opsForList().size(REDIS_TOKEN_PREFIX);
        if (Objects.isNull(size)) {
            return Collections.emptyList();
        }
        return Objects.requireNonNull(redisTemplate.opsForList().range(REDIS_TOKEN_PREFIX, 0, size))
                .stream()
                .map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public void removeToken(String token) {
        redisTemplate.delete(REDIS_TOKEN_PREFIX);
    }

}
