package com.yq.other;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by yangqian on 2019/6/30.
 */
public class SynchronousQueueQuiz {
    public static void main(String[] args) throws Exception {
        BlockingQueue<Integer> queue = new SynchronousQueue<>();
        System.out.print(queue.offer(1) + " ");
        System.out.print(queue.offer(2) + " ");
        System.out.print(queue.offer(3) + " ");
        System.out.print(queue.take() + " ");
        System.out.println(queue.size());
    }
}
