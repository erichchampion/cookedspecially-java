package com.cookedspecially.customerservice.repository;

import com.cookedspecially.customerservice.domain.Customer;
import com.cookedspecially.customerservice.domain.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customer repository
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByCognitoSub(String cognitoSub);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByCognitoSub(String cognitoSub);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Customer> searchCustomers(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status")
    long countByStatus(@Param("status") CustomerStatus status);

    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    Page<Customer> findAllActive(Pageable pageable);
}
