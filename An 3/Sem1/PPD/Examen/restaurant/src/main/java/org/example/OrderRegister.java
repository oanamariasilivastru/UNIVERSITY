package org.example;

import java.util.concurrent.ConcurrentHashMap;

public class OrderRegister {
    private final ConcurrentHashMap<Integer, OrderStatus> register = new ConcurrentHashMap<>();

    public void addOrder(int orderId) {
        register.put(orderId, OrderStatus.IN_ASTEPTARE);
    }

    public void setStatus(int orderId, OrderStatus status) {
        register.put(orderId, status);
    }

    public OrderStatus getStatus(int orderId) {
        return register.get(orderId);
    }

    public int countByStatus(OrderStatus status) {
        int count = 0;
        for (OrderStatus st : register.values()) {
            if (st == status) {
                count++;
            }
        }
        return count;
    }
    public ConcurrentHashMap<Integer, OrderStatus> getSnapshot() {
        return new ConcurrentHashMap<>(register);
    }
}
