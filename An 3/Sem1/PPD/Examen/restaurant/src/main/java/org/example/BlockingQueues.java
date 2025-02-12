package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueues {
    public static final BlockingQueue<Order> Qc = new LinkedBlockingQueue<>(50);
    public static final BlockingQueue<Order> QaSushi = new LinkedBlockingQueue<>(50);
    public static final BlockingQueue<Order> QaPizza = new LinkedBlockingQueue<>(50);
    public static final BlockingQueue<Order> QaPaste = new LinkedBlockingQueue<>(50);

}
