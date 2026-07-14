package com.aizuche.controller;

import com.aizuche.dto.OrderRequest;
import com.aizuche.entity.Order;
import com.aizuche.service.OrderService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result create(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/pickup/{orderId}")
    public Result pickup(@PathVariable Long orderId) {
        try {
            orderService.confirmPickup(orderId);
            return Result.success("确认取车成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/return")
    public Result returnVehicle(@RequestParam Long orderId,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime actualReturnTime,
                                @RequestParam String carCondition) {
        try {
            orderService.returnVehicle(orderId, actualReturnTime, carCondition);
            return Result.success("还车成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/cancel/{orderId}")
    public Result cancel(@PathVariable Long orderId, @RequestParam String reason) {
        try {
            orderService.cancelOrder(orderId, reason);
            return Result.success("订单已取消");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String customerName,
                       @RequestParam(required = false) String vehiclePlate) {
        List<Order> list = orderService.findByConditions(startTime, endTime, status, customerName, vehiclePlate);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/todayPickup")
    public Result todayPickup() {
        return Result.success(orderService.getTodayPickupOrders());
    }

    @GetMapping("/todayReturn")
    public Result todayReturn() {
        return Result.success(orderService.getTodayReturnOrders());
    }

    @PostMapping("/markAbnormal")
    public Result markAbnormal(@RequestParam Long orderId,
                               @RequestParam String type,
                               @RequestParam String remark) {
        orderService.markAbnormal(orderId, type, remark);
        return Result.success("已标记异常");
    }
}