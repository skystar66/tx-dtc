package com.xuliang.netty.loadbalance;

import com.xuliang.lcn.txmsg.loadbalance.RpcLoadBalance;
import com.xuliang.netty.manager.SocketManager;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author xuliang
 */
@Component
public class RandomLoadBalance implements RpcLoadBalance {


    private Random random;

    public RandomLoadBalance() {
        random = new Random();
    }


    @Override
    public String getRemoteKey() throws Exception {
        int size = SocketManager.getInstance().currentSize();
        int randomIndex = random.nextInt(size);
        int index = 0;
        for (Channel channel : SocketManager.getInstance().getChannelGroup()) {
            if (index == randomIndex) {
                return channel.remoteAddress().toString();
            }
            index++;
        }
        return null;
    }
}
