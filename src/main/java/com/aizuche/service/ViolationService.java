package com.aizuche.service;

import com.aizuche.entity.Violation;
import com.aizuche.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    public List<Violation> findAll() {
        return violationRepository.findAll();
    }

    public List<Violation> findByConditions(String orderNo, String customerName, String vehiclePlate) {
        return violationRepository.findByConditions(orderNo, customerName, vehiclePlate);
    }

    public Violation findById(Long id) {
        return violationRepository.findById(id).orElse(null);
    }

    public List<Violation> getPendingViolations() {
        return violationRepository.findByStatus("PENDING");
    }

    @Transactional
    public Violation save(Violation violation) {
        violation.setCreateTime(LocalDateTime.now());
        violation.setUpdateTime(LocalDateTime.now());
        return violationRepository.save(violation);
    }

    @Transactional
    public Violation update(Violation violation) {
        Violation existing = violationRepository.findById(violation.getId()).orElse(null);
        if (existing != null) {
            existing.setOrderNo(violation.getOrderNo());
            existing.setCustomerName(violation.getCustomerName());
            existing.setVehiclePlate(violation.getVehiclePlate());
            existing.setViolationTime(violation.getViolationTime());
            existing.setLocation(violation.getLocation());
            existing.setPoints(violation.getPoints());
            existing.setFineAmount(violation.getFineAmount());
            existing.setRemark(violation.getRemark());
            existing.setUpdateTime(LocalDateTime.now());
            return violationRepository.save(existing);
        }
        return null;
    }

    @Transactional
    public void resolveViolation(Long id, String resolver) {
        Violation violation = findById(id);
        if (violation != null) {
            violation.setStatus("RESOLVED");
            violation.setResolvedTime(LocalDateTime.now());
            violation.setResolver(resolver);
            violation.setUpdateTime(LocalDateTime.now());
            violationRepository.save(violation);
        }
    }

    public List<Object[]> countViolationsByVehicle() {
        return violationRepository.countViolationsByVehicle();
    }

    public List<Object[]> countViolationsByCustomer() {
        return violationRepository.countViolationsByCustomer();
    }
}