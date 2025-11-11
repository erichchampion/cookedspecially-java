package com.cookedspecially.integrationhubservice.repository;

import com.cookedspecially.integrationhubservice.domain.IntegrationConfig;
import com.cookedspecially.integrationhubservice.domain.IntegrationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, Long> {

    Optional<IntegrationConfig> findByPartnerId(String partnerId);

    List<IntegrationConfig> findByRestaurantId(Long restaurantId);

    Optional<IntegrationConfig> findByRestaurantIdAndType(Long restaurantId, IntegrationType type);

    List<IntegrationConfig> findByRestaurantIdAndEnabled(Long restaurantId, Boolean enabled);
}
