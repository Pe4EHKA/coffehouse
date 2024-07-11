package ru.ufanet.test.coffehouse.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ufanet.test.coffehouse.entity.Order;
import ru.ufanet.test.coffehouse.entity.OrderEvent;
import ru.ufanet.test.coffehouse.enums.EventType;
import ru.ufanet.test.coffehouse.repository.OrderEventRepository;
import ru.ufanet.test.coffehouse.repository.OrderRepository;
import ru.ufanet.test.coffehouse.service.OrderService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



import java.time.LocalDateTime;


@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private OrderEvent event;


    @BeforeEach
    void init() {
        event = new OrderEvent();
        event.setOrderId(1);
        event.setEventType(EventType.ACCEPTED);
        event.setEmployeeId(321);
        event.setCreatedAt(LocalDateTime.of(2024,7,11,12,0));
    }

    @Test
    public void shouldPublishEventAndReturnOk() throws Exception {
        mockMvc.perform(post("/coffehouseorders/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":1,\"eventType\":\"ACCEPTED\"" +
                        ",\"employeeId\":321,\"createdAt\":\"2024-07-08T12:00:00Z\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        Mockito.verify(orderService).publishEvent(captor.capture());


    }

    @Test
    public void shouldReturnOrder() throws Exception {
        Order order = new Order();
        order.setId(1);
        order.setStatus(EventType.ACCEPTED.toString());
        when(orderService.findOrder(1)).thenReturn(order);

        mockMvc.perform(get("/coffehouseorders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(EventType.ACCEPTED.toString()));
    }

}