package com.trungquan.nongsan.spec;

import com.trungquan.nongsan.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {

    private ProductSpec() {}

    // keyword: tìm trong title (contains)
    public static Specification<Product> hasKeyword(String keyword) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null; // no filter
            }
            return cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }

    // categoryId: lọc theo danh mục
    public static Specification<Product> hasCategory(Long categoryId) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (categoryId == null) {
                return null;
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    // priceMin & priceMax: lọc theo khoảng giá (originalPrice)
    public static Specification<Product> priceBetween(Double priceMin, Double priceMax) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (priceMin == null && priceMax == null) {
                return null;
            }
            if (priceMin != null && priceMax != null) {
                return cb.between(root.get("originalPrice"), priceMin, priceMax);
            }
            if (priceMin != null) {
                return cb.greaterThanOrEqualTo(root.get("originalPrice"), priceMin);
            }
            return cb.lessThanOrEqualTo(root.get("originalPrice"), priceMax);
        };
    }

    // activeFlag = true: luôn chỉ lấy sản phẩm active
    public static Specification<Product> isActive() {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.isTrue(root.get("activeFlag"));
        };
    }
}
