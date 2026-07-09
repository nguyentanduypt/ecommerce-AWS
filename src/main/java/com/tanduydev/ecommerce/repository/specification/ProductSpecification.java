package com.tanduydev.ecommerce.repository.specification;

import com.tanduydev.ecommerce.dto.request.product.ProductSearchRequest;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.model.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withFilter(ProductSearchRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo tên (Tương đối - LIKE)
            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }

            // 2. Lọc theo Category
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            // 3. Lọc theo Brand
            if (filter.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.getBrandId()));
            }

            // 4. Lọc theo Trạng thái
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("productStatus"), filter.getStatus()));
            }

            // 5. Lọc theo Khoảng giá (Dựa trên ProductVariant)
            if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
                Join<Product, ProductVariant> variants = root.join("variants");
                if (filter.getMinPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(variants.get("price"), filter.getMinPrice()));
                }
                if (filter.getMaxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(variants.get("price"), filter.getMaxPrice()));
                }
                // Quan trọng: Tránh duplicate product nếu có nhiều variant thỏa điều kiện
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}