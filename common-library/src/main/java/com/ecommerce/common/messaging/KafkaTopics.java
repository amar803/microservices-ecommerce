package com.ecommerce.common.messaging;

public final class KafkaTopics {

    public static final String ORDERS_EVENTS = "orders.events";
    public static final String PAYMENTS_EVENTS = "payments.events";
    public static final String PRODUCTS_EVENTS = "products.events";
    public static final String INVENTORY_EVENTS = "inventory.events";

    private KafkaTopics() {
    }
}

