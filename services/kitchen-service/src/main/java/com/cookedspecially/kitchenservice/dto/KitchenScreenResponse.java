package com.cookedspecially.kitchenservice.dto;

import com.cookedspecially.kitchenservice.domain.KitchenScreenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for kitchen screen information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenScreenResponse {
    private Long id;
    private String name;
    private String description;
    private Long fulfillmentCenterId;
    private Long restaurantId;
    private KitchenScreenStatus status;
    private String stationType;
    private String ipAddress;
    private String deviceId;
    private Boolean soundEnabled;
    private Boolean autoAcceptOrders;
    private Integer displayOrder;
    private LocalDateTime lastHeartbeat;
    private Boolean isOnline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
