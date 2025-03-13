package com.example.Handle;

import com.example.MyThreadPool;

public class ThrowRejectHandle implements RejectHandle {
    @Override
    public void reject(Runnable rejectCommand, MyThreadPool threadPool) {
        throw new RuntimeException("阻塞队列已满!");
    }
}
