package ru.ufanet.test.coffehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.test.coffehouse.entity.OrderEvent;

import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Integer> {
    List<OrderEvent> findByOrderId(Integer orderId);
}
