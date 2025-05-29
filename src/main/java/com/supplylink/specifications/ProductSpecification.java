package com.supplylink.specifications;

import com.supplylink.models.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, cb) -> categoryId == null ? null :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasLocation(UUID locationId) {
        return (root, query, cb) -> locationId == null ? null :
                cb.equal(root.get("location").get("id"), locationId);
    }

    public static Specification<Product> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice != null && maxPrice != null)
                return cb.between(root.get("price"), minPrice, maxPrice);
            else if (minPrice != null)
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            else
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Product> hasMinimumQuantity(int minQuantity) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
    }

    public static Specification<Product> createdAfter(Date date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Product> hasMinRating(Double minRating) {
        return (root, query, cb) -> minRating == null ? null :
                cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }
}
