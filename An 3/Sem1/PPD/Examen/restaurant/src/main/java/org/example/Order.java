package org.example;

public class Order {
    private final int idOrder;
    private final String foodType;

    public Order(int id, String foodType) {
        this.idOrder = id;
        this.foodType = foodType;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public String getFoodType() {
        return foodType;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + idOrder +
                ", food='" + foodType + '\'' +
                '}';
    }
}
