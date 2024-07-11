package ru.ufanet.test.coffehouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.ufanet.test.coffehouse.entity.Order;
import ru.ufanet.test.coffehouse.entity.OrderEvent;
import ru.ufanet.test.coffehouse.enums.EventType;
import ru.ufanet.test.coffehouse.repository.OrderEventRepository;
import ru.ufanet.test.coffehouse.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private OrderEventRepository orderEventRepository;
    private OrderService orderService;

    @BeforeEach
    void init() {
        orderRepository = mock(OrderRepository.class);
        orderEventRepository = mock(OrderEventRepository.class);
        orderService = new OrderServiceImpl(orderRepository, orderEventRepository);
    }

    @Test
    public void shouldSaveEventWhenOrderExistsAndNotCompleted() {
        Order order = new Order();
        order.setId(1);
        order.setCompleted(false);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(1);
        orderEvent.setEventType(EventType.ACCEPTED);

        orderService.publishEvent(orderEvent);
        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(orderEventRepository).save(captor.capture());

        assertEquals(1, captor.getValue().getOrderId());
        assertEquals(EventType.ACCEPTED, captor.getValue().getEventType());
        assertFalse(order.isCompleted());
    }

    @Test
    public void shouldThrowExceptionWhenOrderDoesNotExists() {
        when(orderRepository.existsById(1)).thenReturn(false);

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(1);

        assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(orderEvent));
    }

    @Test
    public void shouldThrowExceptionWhenOrderIsCompleted() {
        Order order = new Order();
        order.setId(1);
        order.setCompleted(true);
        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(1);

        assertThrows(IllegalStateException.class, () -> orderService.publishEvent(orderEvent));
    }

    @Test
    public void shouldReturnOrderWithEvents() {
        Order order = new Order();
        order.setId(1);
        OrderEvent orderEvent1 = new OrderEvent();
        orderEvent1.setOrderId(1);
        orderEvent1.setEventType(EventType.REGISTERED);
        OrderEvent orderEvent2 = new OrderEvent();
        orderEvent2.setOrderId(1);
        orderEvent2.setEventType(EventType.ACCEPTED);
        order.setEvents(List.of(orderEvent1, orderEvent2));

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderEventRepository.findByOrderId(1)).thenReturn(List.of(orderEvent1, orderEvent2));

        Order resultOrder = orderService.findOrder(1);
        assertEquals(1, resultOrder.getId());
        assertEquals(order.getEvents(), resultOrder.getEvents());
        assertEquals(EventType.ACCEPTED.toString(), resultOrder.getStatus());
    }
}