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
import java.util.List;

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
        // 返回近7天的订单趋势
        return Result.success("订单趋势数据");
    }

    @GetMapping("/vehicleUtilization")
    public Result vehicleUtilization() {
        // 返回车辆出租率
        return Result.success("车辆出租率数据");
    }
}