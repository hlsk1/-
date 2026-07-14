package com.aizuche.service;

import com.aizuche.dto.OrderRequest;
import com.aizuche.entity.Customer;
import com.aizuche.entity.Order;
import com.aizuche.entity.Vehicle;
import com.aizuche.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VehicleService vehicleService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByConditions(LocalDateTime startTime, LocalDateTime endTime,
                                        String status, String customerName, String vehiclePlate) {
        return orderRepository.findByConditions(startTime, endTime, status, customerName, vehiclePlate);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getTodayPickupOrders() {
        return orderRepository.findTodayPickupOrders();
    }

    public List<Order> getTodayReturnOrders() {
        return orderRepository.findTodayReturnOrders();
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        Customer customer = customerService.findById(request.getCustomerId());
        Vehicle vehicle = vehicleService.findById(request.getVehicleId());

        if (customer == null || vehicle == null) {
            throw new RuntimeException("客户或车辆不存在");
        }

        if (!"AVAILABLE".equals(vehicle.getStatus())) {
            throw new RuntimeException("车辆当前不可用");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setCustomerId(customer.getId());
        order.setVehicleId(vehicle.getId());
        order.setCustomerName(customer.getName());
        order.setCustomerPhone(customer.getPhone());
        order.setVehicleBrand(vehicle.getBrand());
        order.setVehicleModel(vehicle.getModel());
        order.setVehiclePlate(vehicle.getPlateNumber());
        order.setPickupTime(request.getPickupTime());
        order.setReturnTime(request.getReturnTime());
        order.setDailyPrice(vehicle.getDailyPrice());
        order.setRemark(request.getRemark());
        order.setStatus("PENDING");

        // 计算租期和费用
        long days = ChronoUnit.DAYS.between(request.getPickupTime(), request.getReturnTime());
        days = days == 0 ? 1 : days;
        order.setRentDays((int) days);

        BigDecimal totalAmount = vehicle.getDailyPrice().multiply(BigDecimal.valueOf(days));
        order.setTotalAmount(totalAmount);

        // 押金 = 总金额 * 30%
        BigDecimal deposit = totalAmount.multiply(BigDecimal.valueOf(0.3));
        order.setDepositAmount(deposit);

        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    public void confirmPickup(Long orderId) {
        Order order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("PICKED_UP");
        order.setUpdateTime(LocalDateTime.now());
        orderRepository.save(order);

        // 更新车辆状态
        Vehicle vehicle = vehicleService.findById(order.getVehicleId());
        if (vehicle != null) {
            vehicle.setStatus("RENTED");
            vehicle.setUpdateTime(LocalDateTime.now());
            // 需要注入VehicleRepository或使用VehicleService
        }
    }

    @Transactional
    public void returnVehicle(Long orderId, LocalDateTime actualReturnTime, String carCondition) {
        Order order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PICKED_UP".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setActualReturnTime(actualReturnTime);
        order.setCarCondition(carCondition);
        order.setStatus("RETURNED");
        order.setUpdateTime(LocalDateTime.now());

        // 计算最终费用
        long actualDays = ChronoUnit.DAYS.between(order.getPickupTime(), actualReturnTime);
        actualDays = actualDays == 0 ? 1 : actualDays;
        BigDecimal finalAmount = order.getDailyPrice().multiply(BigDecimal.valueOf(actualDays));
        order.setFinalAmount(finalAmount);

        orderRepository.save(order);

        // 更新车辆状态
        Vehicle vehicle = vehicleService.findById(order.getVehicleId());
        if (vehicle != null) {
            vehicle.setStatus("AVAILABLE");
            vehicle.setUpdateTime(LocalDateTime.now());
            // 需要注入VehicleRepository或使用VehicleService
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus()) && !"PICKED_UP".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法取消");
        }

        order.setStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setUpdateTime(LocalDateTime.now());
        orderRepository.save(order);

        // 如果是已取车状态，需要恢复车辆状态
        if ("PICKED_UP".equals(order.getStatus())) {
            Vehicle vehicle = vehicleService.findById(order.getVehicleId());
            if (vehicle != null) {
                vehicle.setStatus("AVAILABLE");
                vehicle.setUpdateTime(LocalDateTime.now());
            }
        }
    }

    @Transactional
    public void markAbnormal(Long orderId, String type, String remark) {
        Order order = findById(orderId);
        if (order != null) {
            order.setRemark("【异常】" + type + ": " + remark);
            order.setUpdateTime(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "R" + date + uuid;
    }
}