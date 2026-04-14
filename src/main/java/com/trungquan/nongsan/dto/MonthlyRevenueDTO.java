package com.trungquan.nongsan.dto;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

@Data
@Value
public class MonthlyRevenueDTO implements Serializable {
    private int month;
    private double revenue;

    public MonthlyRevenueDTO(int month, double revenue) {
        this.month = month;
        this.revenue = revenue;
    }
}
