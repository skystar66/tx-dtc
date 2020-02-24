package com.xuliang.lcn.common.runner;


/**
 * Description: TxLcn run control
 * Company: CodingApi
 * Date: 2020/1/16
 *
 * @author xuliang
 * @desc:初始化
 */
public interface TxLcnInitializer {

    /**
     * init 初始化
     *
     * @throws Exception Throwable
     */
    default void init() throws Exception {
    }

    /**
     * destroy 销毁
     *
     * @throws Exception Throwable
     */
    default void destroy() throws Exception {
    }

    /**
     * order 执行顺序
     *
     * @return int
     */
    default int order() {
        return 0;
    }

}
