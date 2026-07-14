package com.aizuche.controller;

import com.aizuche.entity.Vehicle;
import com.aizuche.service.VehicleService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String brand,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice) {
        List<Vehicle> list = vehicleService.findByConditions(brand, status, minPrice, maxPrice);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null) {
            return Result.error("车辆不存在");
        }
        return Result.success(vehicle);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Vehicle vehicle) {
        vehicleService.save(vehicle);
        return Result.success("新增车辆成功");
    }

    @PutMapping("/edit/{id}")
    public Result edit(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        Vehicle updated = vehicleService.update(vehicle);
        if (updated == null) {
            return Result.error("车辆不存在");
        }
        return Result.success("修改车辆信息成功");
    }

    @PostMapping("/parking")
    public Result parking(@RequestParam Long id,
                          @RequestParam String parkingSpot,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime parkingTime) {
        vehicleService.parking(id, parkingSpot, parkingTime);
        return Result.success("车辆入库成功");
    }

    @PostMapping("/repair")
    public Result repair(@RequestParam Long id,
                         @RequestParam String reason,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expectedTime) {
        vehicleService.setRepair(id, reason, expectedTime);
        return Result.success("车辆已设为维修状态");
    }

    @PostMapping("/repairComplete/{id}")
    public Result repairComplete(@PathVariable Long id) {
        vehicleService.completeRepair(id);
        return Result.success("维修完成，车辆已恢复可用");
    }

    @GetMapping("/statusSummary")
    public Result statusSummary() {
        return Result.success(vehicleService.getStatusSummary());
    }

    @GetMapping("/export")
    public Result export(@RequestParam(required = false) String fields) {
        String filePath = vehicleService.exportToExcel(fields);
        return Result.success(filePath);
    }
}