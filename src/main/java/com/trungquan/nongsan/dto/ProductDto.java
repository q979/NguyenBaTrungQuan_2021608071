package com.trungquan.nongsan.dto;

import com.trungquan.nongsan.entity.Product;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
@Data
@Value
public class ProductDto implements Serializable {
    String title;
    Double totalRevenue;

    public ProductDto(String title, Double totalRevenue) {
        this.title = title;
        this.totalRevenue = totalRevenue;
    }
}