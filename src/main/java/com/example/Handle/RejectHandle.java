package com.example.Handle;

import com.example.MyThreadPool;

/**
 * 拒绝策略
 */
public interface RejectHandle {
    void reject(Runnable rejectCommand, MyThreadPool threadPool);
}
