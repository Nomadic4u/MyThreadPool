package com.example;

import com.example.Handle.RejectHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    public MyThreadPool(int corePoolSize, int maxSize, int timeOut, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectHandle rejectHandle) {
        this.corePoolSize = corePoolSize;
        this.maxSize = maxSize;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHandle = rejectHandle;
    }

    public BlockingQueue<Runnable> blockingQueue;

    // 核心线程数
    private int corePoolSize = 10;

    //最大线程数
    private int maxSize = 16;

    private int timeOut = 1;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    // 拒绝策略
    private final RejectHandle rejectHandle;

    // 核心线程
    List<Thread> coreList = new ArrayList<>();

    // 辅助线程
    List<Thread> supportList = new ArrayList<>();


    public void execute(Runnable command) {
        if (coreList.size() < corePoolSize) {
            Thread thread = new CoreThread();
            coreList.add(thread);
            thread.start();
        }
        if (blockingQueue.offer(command)) {
            return;
        }

        if (coreList.size() + supportList.size() < maxSize) {
            Thread thread = new SupportThread();
            supportList.add(thread);
            thread.start();
        }

        if(!blockingQueue.offer(command)) {
            rejectHandle.reject(command, this);
//            throw new RuntimeException("阻塞队列满了" + blockingQueue.size());
        }
    }

    class CoreThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    class SupportThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable command = blockingQueue.poll(timeOut, timeUnit);
                    if(command == null) {
                        break;
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + "线程已关闭");
        }
    }
}
