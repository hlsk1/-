package com.aizuche.repository;

import com.aizuche.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:brand IS NULL OR v.brand LIKE %:brand%) AND " +
            "(:status IS NULL OR v.status = :status) AND " +
            "(:minPrice IS NULL OR v.dailyPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR v.dailyPrice <= :maxPrice)")
    List<Vehicle> findByConditions(@Param("brand") String brand,
                                   @Param("status") String status,
                                   @Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice);

    List<Vehicle> findByStatus(String status);

    long countByStatus(String status);
}