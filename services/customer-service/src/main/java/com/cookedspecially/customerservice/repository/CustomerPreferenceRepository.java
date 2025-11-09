package com.cookedspecially.customerservice.repository;

import com.cookedspecially.customerservice.domain.CustomerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customer preference repository
 */
@Repository
public interface CustomerPreferenceRepository extends JpaRepository<CustomerPreference, Long> {

    Optional<CustomerPreference> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long customerId);
}
