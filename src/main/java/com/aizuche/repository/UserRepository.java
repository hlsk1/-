package com.aizuche.repository;

import com.aizuche.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) OR " +
            "(:phone IS NULL OR u.phone LIKE %:phone%)")
    List<User> findByConditions(@Param("username") String username,
                                @Param("phone") String phone);

    boolean existsByUsername(String username);
}