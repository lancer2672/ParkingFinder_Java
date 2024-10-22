package com.project.parkingfinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByName(String name);
    List<User> findByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);
    
    @Query(value = "SELECT * FROM users WHERE role_id = :roleId", 
           countQuery = "SELECT count(*) FROM users WHERE role_id = :roleId",
           nativeQuery = true)
    Page<User> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

}
