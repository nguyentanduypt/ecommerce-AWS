package com.tanduydev.ecommerce.repository.specification;

import com.tanduydev.ecommerce.dto.request.order.OrderSearchRequest;
import com.tanduydev.ecommerce.model.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> withFilter(OrderSearchRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getOrderCode() != null && !filter.getOrderCode().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("orderCode")), "%" + filter.getOrderCode().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), filter.getPaymentMethod()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}