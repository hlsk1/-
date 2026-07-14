package com.aizuche.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatisticsResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Long activeVehicles;
    private Long totalCustomers;
}