package ru.ufanet.test.coffehouse.service;

import org.springframework.stereotype.Service;
import ru.ufanet.test.coffehouse.entity.Order;
import ru.ufanet.test.coffehouse.entity.OrderEvent;
import ru.ufanet.test.coffehouse.enums.EventType;
import ru.ufanet.test.coffehouse.repository.OrderEventRepository;
import ru.ufanet.test.coffehouse.repository.OrderRepository;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    @Override
    public void publishEvent(OrderEvent event) {
        if (!orderRepository.existsById(event.getOrderId())) {
            throw new IllegalArgumentException("Заказ должен быть зарегистрирован, перед публикацией нового события");
        }
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        if (order.isCompleted()) {
            throw new IllegalStateException("Новые события добавлять нельзя, заказ уже завершен");
        }
        orderEventRepository.save(event);

        if (EventType.CANCELLED.equals(event.getEventType()) || EventType.ISSUED.equals(event.getEventType())) {
            order.setCompleted(true);
            orderRepository.save(order);
        }
    }

    @Override
    public Order findOrder(int id) {
        Order order = orderRepository.findById(id).orElseThrow();
        List<OrderEvent> orderEvents = orderEventRepository.findByOrderId(id);
        order.setEvents(orderEvents);

        String status = EventType.REGISTERED.toString();
        for (OrderEvent orderEvent : orderEvents) {
            if (EventType.CANCELLED.equals(orderEvent.getEventType())) {
                status = EventType.CANCELLED.toString();
                break;
            } else if (EventType.ISSUED.equals(orderEvent.getEventType())) {
                status = EventType.ISSUED.toString();
                break;
            } else if (EventType.ACCEPTED.equals(orderEvent.getEventType())) {
                status = EventType.ACCEPTED.toString();
            } else if (EventType.READY.equals(orderEvent.getEventType())) {
                status = EventType.READY.toString();
            }
        }
        order.setStatus(status);
        return order;
    }
}
