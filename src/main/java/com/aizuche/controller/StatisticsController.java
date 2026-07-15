package com.aizuche.controller;

import com.aizuche.dto.StatisticsResponse;
import com.aizuche.entity.Order;
import com.aizuche.entity.Vehicle;
import com.aizuche.service.CustomerService;
import com.aizuche.service.OrderService;
import com.aizuche.service.VehicleService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/overview")
    public Result overview() {
        List<Order> allOrders = orderService.findAll();
        List<Vehicle> allVehicles = vehicleService.findAll();

        StatisticsResponse response = new StatisticsResponse();
        response.setTotalOrders((long) allOrders.size());

        BigDecimal revenue = allOrders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()) || "RETURNED".equals(o.getStatus()))
                .map(o -> o.getFinalAmount() != null ? o.getFinalAmount() : o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalRevenue(revenue);

        long completedCount = allOrders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .count();
        // 利润简化计算
        response.setTotalProfit(revenue.multiply(BigDecimal.valueOf(0.3)));

        response.setActiveVehicles(vehicleService.countByStatus("AVAILABLE"));
        response.setTotalCustomers((long) customerService.findAll().size());

        return Result.success(response);
    }

    @GetMapping("/orderTrend")
    public Result orderTrend() {
        List<Order> allOrders = orderService.findAll();
        // 按日期聚合近14天订单数
        Map<String, Long> dailyCount = new LinkedHashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 13; i >= 0; i--) {
            dailyCount.put(today.minusDays(i).toString(), 0L);
        }
        for (Order o : allOrders) {
            if (o.getCreateTime() != null) {
                String date = o.getCreateTime().toLocalDate().toString();
                dailyCount.computeIfPresent(date, (k, v) -> v + 1);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dailyCount.keySet());
        result.put("counts", dailyCount.values());
        return Result.success(result);
    }

    @GetMapping("/vehicleUtilization")
    public Result vehicleUtilization() {
        long total = vehicleService.findAll().size();
        long rented = vehicleService.countByStatus("RENTED");
        long available = vehicleService.countByStatus("AVAILABLE");
        long repair = vehicleService.countByStatus("REPAIR");
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rented", rented);
        result.put("available", available);
        result.put("repair", repair);
        result.put("utilizationRate", total > 0 ? Math.round((double) rented / total * 100) : 0);
        return Result.success(result);
    }
}