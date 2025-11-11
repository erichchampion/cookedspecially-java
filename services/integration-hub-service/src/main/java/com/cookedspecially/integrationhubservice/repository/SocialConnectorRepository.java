package com.cookedspecially.integrationhubservice.repository;

import com.cookedspecially.integrationhubservice.domain.ConnectorType;
import com.cookedspecially.integrationhubservice.domain.SocialConnector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialConnectorRepository extends JpaRepository<SocialConnector, Long> {

    List<SocialConnector> findByRestaurantId(Long restaurantId);

    Optional<SocialConnector> findByRestaurantIdAndType(Long restaurantId, ConnectorType type);

    List<SocialConnector> findByRestaurantIdAndEnabled(Long restaurantId, Boolean enabled);
}
