package com.cookedspecially.integrationhubservice.controller;

import com.cookedspecially.integrationhubservice.dto.zomato.*;
import com.cookedspecially.integrationhubservice.service.ZomatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/integrations/zomato")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Zomato Integration", description = "Zomato order and menu integration endpoints")
public class ZomatoController {

    private final ZomatoService zomatoService;

    @PostMapping("/webhook/order")
    @Operation(summary = "Receive Zomato order webhook", description = "Public webhook endpoint to receive orders from Zomato")
    public ResponseEntity<Map<String, Object>> receiveOrderWebhook(
            @Valid @RequestBody ZomatoOrderDTO orderDTO,
            @RequestHeader(value = "X-Zomato-Signature", required = false) String signature,
            @RequestHeader Map<String, String> headers) {

        log.info("Received Zomato order webhook: orderId={}", orderDTO.getOrderId());

        try {
            Map<String, Object> result = zomatoService.processOrderWebhook(orderDTO, signature, headers);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing Zomato order webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/confirm")
    @Operation(summary = "Confirm order to Zomato", description = "Confirm order acceptance to Zomato")
    public ResponseEntity<Map<String, Object>> confirmOrder(
            @PathVariable String orderId,
            @Valid @RequestBody ZomatoOrderConfirmDTO confirmDTO) {

        log.info("Confirming Zomato order: orderId={}", orderId);

        try {
            Map<String, Object> result = zomatoService.confirmOrder(confirmDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error confirming Zomato order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/reject")
    @Operation(summary = "Reject order to Zomato", description = "Reject order to Zomato with reason")
    public ResponseEntity<Map<String, Object>> rejectOrder(
            @PathVariable String orderId,
            @Valid @RequestBody ZomatoOrderRejectDTO rejectDTO) {

        log.info("Rejecting Zomato order: orderId={}", orderId);

        try {
            Map<String, Object> result = zomatoService.rejectOrder(rejectDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error rejecting Zomato order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update order status to Zomato")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody ZomatoOrderStatusUpdateDTO statusDTO) {

        log.info("Updating Zomato order status: orderId={}", orderId);

        try {
            Map<String, Object> result = zomatoService.updateOrderStatus(statusDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error updating Zomato order status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/menu/sync")
    @Operation(summary = "Sync menu to Zomato", description = "Synchronize restaurant menu to Zomato")
    public ResponseEntity<Map<String, Object>> syncMenu(
            @Valid @RequestBody ZomatoMenuSyncDTO menuDTO) {

        log.info("Syncing menu to Zomato: restaurantId={}", menuDTO.getRestaurantId());

        try {
            Map<String, Object> result = zomatoService.syncMenu(menuDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error syncing menu to Zomato", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/restaurant/status")
    @Operation(summary = "Update restaurant status", description = "Update restaurant open/close status in Zomato")
    public ResponseEntity<Map<String, Object>> updateRestaurantStatus(
            @Valid @RequestBody ZomatoRestaurantStatusDTO statusDTO) {

        log.info("Updating restaurant status in Zomato: restaurantId={}", statusDTO.getRestaurantId());

        try {
            Map<String, Object> result = zomatoService.updateRestaurantStatus(statusDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error updating restaurant status in Zomato", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
