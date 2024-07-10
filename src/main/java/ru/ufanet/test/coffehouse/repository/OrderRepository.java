package ru.ufanet.test.coffehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.test.coffehouse.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
