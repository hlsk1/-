package com.aizuche.repository;

import com.aizuche.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    List<Violation> findByStatus(String status);

    List<Violation> findByCustomerNameContaining(String customerName);

    @Query("SELECT v FROM Violation v WHERE " +
            "(:orderNo IS NULL OR v.orderNo = :orderNo) OR " +
            "(:customerName IS NULL OR v.customerName LIKE %:customerName%) OR " +
            "(:vehiclePlate IS NULL OR v.vehiclePlate LIKE %:vehiclePlate%)")
    List<Violation> findByConditions(@Param("orderNo") String orderNo,
                                     @Param("customerName") String customerName,
                                     @Param("vehiclePlate") String vehiclePlate);

    @Query("SELECT v.vehiclePlate, COUNT(v) as count FROM Violation v GROUP BY v.vehiclePlate ORDER BY count DESC")
    List<Object[]> countViolationsByVehicle();

    @Query("SELECT v.customerName, COUNT(v) as count FROM Violation v GROUP BY v.customerName ORDER BY count DESC")
    List<Object[]> countViolationsByCustomer();
}