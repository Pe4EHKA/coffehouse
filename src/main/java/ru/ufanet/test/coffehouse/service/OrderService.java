package ru.ufanet.test.coffehouse.service;

import ru.ufanet.test.coffehouse.entity.Order;
import ru.ufanet.test.coffehouse.entity.OrderEvent;

public interface OrderService {

    void publishEvent(OrderEvent event);

    Order findOrder(int id);
}
