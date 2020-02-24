package com.xuliang.lcn.common.util;

import org.springframework.util.DigestUtils;


/**
 * Description: 事务相关工具类
 * Date: 2018/12/17
 *
 * @author xuliang
 */
public class Transactions {

    public static String APPLICATION_ID_WHEN_RUNNING;

    /**
     * 分布式事务类型 Properties传递参数key
     */
    public static final String DTX_TYPE = "DTX_TYPE";

    /**
     * 分布式事务传播类型 Properties传递参数key
     */
    public static final String DTX_PROPAGATION = "DTX_PROPAGATION";



    /**
     * 方法签名生成事务单元ID
     *
     * @param methodSignature 方法签名key
     * @return md5hex val
     */
    public static String unitId(String methodSignature) {
        return DigestUtils.md5DigestAsHex((APPLICATION_ID_WHEN_RUNNING + methodSignature).getBytes());
    }


}
