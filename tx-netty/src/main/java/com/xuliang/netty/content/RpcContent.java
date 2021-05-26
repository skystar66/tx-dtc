package com.xuliang.netty.content;

import com.xuliang.lcn.txmsg.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RpcContent {


    private long milliseconds;

    private volatile MessageDto res;


    private Condition condition;


    private Lock lock;

    private volatile boolean used = false;

    public void init() {
        used = true;
    }


    public void clear() {
        used = false;
        res = null;
    }


    public boolean isUsed() {
        return used;
    }

    public RpcContent(long milliseconds) {
        this.milliseconds = milliseconds;
        lock = new ReentrantLock(true);
        condition = lock.newCondition();
    }

    public void await() {
        await(milliseconds);
    }


    public void await(long timeout) {
        try {
            lock.lock();
            try {
                condition.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public MessageDto getRes() {

        synchronized (this) {
            return res;
        }

    }

    public void setRes(MessageDto res) {
        synchronized (this) {
            this.res = res;
        }
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

}
