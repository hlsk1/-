package com.aizuche.service;

import com.aizuche.entity.Vehicle;
import com.aizuche.repository.VehicleRepository;
import com.aizuche.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findByConditions(String brand, String status, Double minPrice, Double maxPrice) {
        return vehicleRepository.findByConditions(brand, status, minPrice, maxPrice);
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) {
        vehicle.setCreateTime(LocalDateTime.now());
        vehicle.setUpdateTime(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Vehicle vehicle) {
        Vehicle existing = vehicleRepository.findById(vehicle.getId()).orElse(null);
        if (existing != null) {
            existing.setBrand(vehicle.getBrand());
            existing.setModel(vehicle.getModel());
            existing.setPlateNumber(vehicle.getPlateNumber());
            existing.setColor(vehicle.getColor());
            existing.setSeatCount(vehicle.getSeatCount());
            existing.setDailyPrice(vehicle.getDailyPrice());
            existing.setStatus(vehicle.getStatus());
            existing.setParkingSpot(vehicle.getParkingSpot());
            existing.setParkingTime(vehicle.getParkingTime());
            existing.setDescription(vehicle.getDescription());
            existing.setImageUrls(vehicle.getImageUrls());
            existing.setUpdateTime(LocalDateTime.now());
            return vehicleRepository.save(existing);
        }
        return null;
    }

    @Transactional
    public void parking(Long id, String parkingSpot, LocalDateTime parkingTime) {
        Vehicle vehicle = findById(id);
        if (vehicle != null) {
            vehicle.setParkingSpot(parkingSpot);
            vehicle.setParkingTime(parkingTime);
            vehicle.setStatus("AVAILABLE");
            vehicle.setUpdateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }
    }

    @Transactional
    public void setRepair(Long id, String reason, LocalDateTime expectedTime) {
        Vehicle vehicle = findById(id);
        if (vehicle != null) {
            vehicle.setStatus("REPAIR");
            vehicle.setRepairReason(reason);
            vehicle.setRepairExpectedTime(expectedTime);
            vehicle.setUpdateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }
    }

    @Transactional
    public void completeRepair(Long id) {
        Vehicle vehicle = findById(id);
        if (vehicle != null) {
            vehicle.setStatus("AVAILABLE");
            vehicle.setRepairReason(null);
            vehicle.setRepairExpectedTime(null);
            vehicle.setUpdateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }
    }

    public Map<String, Object> getStatusSummary() {
        List<Vehicle> all = vehicleRepository.findAll();
        Map<String, Long> statusCount = all.stream()
                .collect(Collectors.groupingBy(v -> v.getStatus() != null ? v.getStatus() : "UNKNOWN", Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("statusCount", statusCount);
        result.put("total", all.size());

        Map<String, List<Vehicle>> groupByStatus = all.stream()
                .collect(Collectors.groupingBy(v -> v.getStatus() != null ? v.getStatus() : "UNKNOWN"));
        result.put("details", groupByStatus);

        return result;
    }

    public String exportToExcel(String fields) {
        List<Vehicle> list = vehicleRepository.findAll();
        String[] selectedFields = fields != null ? fields.split(",") :
                new String[]{"brand", "model", "plateNumber", "color", "seatCount", "dailyPrice", "status"};
        return ExcelUtil.exportVehicleList(list, selectedFields);
    }

    public long countByStatus(String status) {
        return vehicleRepository.countByStatus(status);
    }
}