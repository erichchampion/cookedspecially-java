package com.cookedspecially.customerservice.repository;

import com.cookedspecially.customerservice.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Address repository
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCustomerId(Long customerId);

    Optional<Address> findByCustomerIdAndIsDefaultTrue(Long customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId AND a.id = :addressId")
    Optional<Address> findByCustomerIdAndId(@Param("customerId") Long customerId,
                                              @Param("addressId") Long addressId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId AND a.addressType = :type")
    List<Address> findByCustomerIdAndAddressType(@Param("customerId") Long customerId,
                                                   @Param("type") String type);
}
