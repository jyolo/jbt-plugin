package com.common.tools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    public void addTask(Runnable task) {
        taskQueue.offer(task);
        processTasks();
    }

    private void processTasks() {
        while (!taskQueue.isEmpty()) {
            Runnable task = taskQueue.poll();
            if (task != null) {
                task.run();
            }
        }
    }
}
