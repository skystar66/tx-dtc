package com.xuliang.netty.imply;


import com.xuliang.netty.em.NettyType;

/**
 * Description:
 * Company: CodingApi
 * Date: 2020/12/21
 *
 * @author xulia
 */
public class NettyContext {

    protected static NettyType nettyType;


    public static NettyType currentType() {
        return nettyType;
    }

    protected static Object params;

    public static <T> T currentParam(Class<T> tClass) {
        return (T) params;
    }


}
