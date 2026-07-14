package com.aizuche.controller;

import com.aizuche.entity.Violation;
import com.aizuche.service.ViolationService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violation")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String orderNo,
                       @RequestParam(required = false) String customerName,
                       @RequestParam(required = false) String vehiclePlate) {
        List<Violation> list = violationService.findByConditions(orderNo, customerName, vehiclePlate);
        return Result.success(list);
    }

    @GetMapping("/pending")
    public Result pending() {
        return Result.success(violationService.getPendingViolations());
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Violation violation = violationService.findById(id);
        if (violation == null) {
            return Result.error("违章记录不存在");
        }
        return Result.success(violation);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Violation violation) {
        violationService.save(violation);
        return Result.success("违章记录创建成功");
    }

    @PutMapping("/edit/{id}")
    public Result edit(@PathVariable Long id, @RequestBody Violation violation) {
        violation.setId(id);
        Violation updated = violationService.update(violation);
        if (updated == null) {
            return Result.error("违章记录不存在");
        }
        return Result.success("修改成功");
    }

    @PostMapping("/resolve/{id}")
    public Result resolve(@PathVariable Long id, @RequestParam String resolver) {
        violationService.resolveViolation(id, resolver);
        return Result.success("已标记为已处理");
    }

    @GetMapping("/statistics/byVehicle")
    public Result statisticsByVehicle() {
        return Result.success(violationService.countViolationsByVehicle());
    }

    @GetMapping("/statistics/byCustomer")
    public Result statisticsByCustomer() {
        return Result.success(violationService.countViolationsByCustomer());
    }
}