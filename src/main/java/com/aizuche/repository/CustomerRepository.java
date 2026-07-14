package com.aizuche.repository;

import com.aizuche.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:name IS NULL OR c.name LIKE %:name%) OR " +
            "(:phone IS NULL OR c.phone LIKE %:phone%)")
    List<Customer> findByConditions(@Param("name") String name,
                                    @Param("phone") String phone);

    List<Customer> findByStatus(String status);
}