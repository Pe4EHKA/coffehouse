package ru.ufanet.test.coffehouse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.ufanet.test.coffehouse.entity.Order;
import ru.ufanet.test.coffehouse.entity.OrderEvent;
import ru.ufanet.test.coffehouse.service.OrderService;

@Controller
@RequestMapping("/coffehouseorders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> publishEvent(@RequestBody OrderEvent orderEvent) {
        orderService.publishEvent(orderEvent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        Order order = orderService.findOrder(id);
        return ResponseEntity.ok().body(order);
    }
}
