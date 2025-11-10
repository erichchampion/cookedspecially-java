package com.cookedspecially.kitchenservice.controller;

import com.cookedspecially.kitchenservice.domain.Till;
import com.cookedspecially.kitchenservice.domain.TillHandover;
import com.cookedspecially.kitchenservice.domain.TillTransaction;
import com.cookedspecially.kitchenservice.dto.*;
import com.cookedspecially.kitchenservice.service.TillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Till/Cash Register operations
 */
@RestController
@RequestMapping("/api/v1/tills")
@RequiredArgsConstructor
@Tag(name = "Till Management", description = "Cash register and till operations")
@SecurityRequirement(name = "bearer-jwt")
public class TillController {

    private final TillService tillService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Create a new till")
    public ResponseEntity<TillResponse> createTill(@Valid @RequestBody CreateTillRequest request) {
        Till till = Till.builder()
            .name(request.getName())
            .description(request.getDescription())
            .fulfillmentCenterId(request.getFulfillmentCenterId())
            .restaurantId(request.getRestaurantId())
            .build();

        Till created = tillService.createTill(till);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Get till by ID")
    public ResponseEntity<TillResponse> getTillById(@PathVariable Long id) {
        Till till = tillService.getTillById(id);
        return ResponseEntity.ok(toResponse(till));
    }

    @GetMapping("/fulfillment-center/{fcId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'MANAGER')")
    @Operation(summary = "Get tills by fulfillment center")
    public ResponseEntity<List<TillResponse>> getTillsByFulfillmentCenter(@PathVariable Long fcId) {
        List<Till> tills = tillService.getTillsByFulfillmentCenter(fcId);
        return ResponseEntity.ok(tills.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Get tills by restaurant")
    public ResponseEntity<List<TillResponse>> getTillsByRestaurant(@PathVariable Long restaurantId) {
        List<Till> tills = tillService.getTillsByRestaurant(restaurantId);
        return ResponseEntity.ok(tills.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Open till for shift")
    public ResponseEntity<TillResponse> openTill(@PathVariable Long id,
                                                  @Valid @RequestBody OpenTillRequest request) {
        Till till = tillService.openTill(id, request.getOpeningBalance(),
            request.getUserId(), request.getUserName());
        return ResponseEntity.ok(toResponse(till));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Close till for shift")
    public ResponseEntity<TillResponse> closeTill(@PathVariable Long id,
                                                   @Valid @RequestBody CloseTillRequest request) {
        Till till = tillService.closeTill(id, request.getClosingBalance());
        return ResponseEntity.ok(toResponse(till));
    }

    @PostMapping("/{id}/cash/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Add cash to till")
    public ResponseEntity<Void> addCash(@PathVariable Long id,
                                        @Valid @RequestBody TillCashRequest request) {
        tillService.addCash(id, request.getAmount(), request.getNotes(),
            request.getPerformedBy(), request.getPerformedByName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cash/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Withdraw cash from till")
    public ResponseEntity<Void> withdrawCash(@PathVariable Long id,
                                             @Valid @RequestBody TillCashRequest request) {
        tillService.withdrawCash(id, request.getAmount(), request.getNotes(),
            request.getPerformedBy(), request.getPerformedByName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM', 'CASHIER')")
    @Operation(summary = "Record a sale transaction")
    public ResponseEntity<Void> recordSale(@PathVariable Long id,
                                           @Valid @RequestBody RecordSaleRequest request) {
        tillService.recordSale(id, request.getOrderId(), request.getAmount(),
            request.getPaymentMethod(), request.getPerformedBy(), request.getPerformedByName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Get till balance")
    public ResponseEntity<BigDecimal> getTillBalance(@PathVariable Long id) {
        BigDecimal balance = tillService.getTillBalance(id);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Get till transactions")
    public ResponseEntity<List<TillTransaction>> getTillTransactions(@PathVariable Long id) {
        List<TillTransaction> transactions = tillService.getTillTransactions(id);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}/transactions/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get till transactions by date range")
    public ResponseEntity<List<TillTransaction>> getTillTransactionsByRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TillTransaction> transactions = tillService.getTillTransactionsByDateRange(id, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}/sales/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Calculate total sales for till")
    public ResponseEntity<BigDecimal> calculateTotalSales(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = tillService.calculateTotalSales(id, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{id}/handover")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Initiate till handover")
    public ResponseEntity<TillHandoverResponse> initiateHandover(@PathVariable Long id,
                                                                 @Valid @RequestBody TillHandoverRequest request) {
        TillHandover handover = tillService.initiateHandover(id,
            request.getFromUserId(), request.getFromUserName(),
            request.getToUserId(), request.getToUserName(),
            request.getActualBalance(), request.getNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(toHandoverResponse(handover));
    }

    @PostMapping("/handovers/{handoverId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve till handover")
    public ResponseEntity<TillHandoverResponse> approveHandover(@PathVariable Long handoverId,
                                                                @RequestParam String approverId) {
        TillHandover handover = tillService.approveHandover(handoverId, approverId);
        return ResponseEntity.ok(toHandoverResponse(handover));
    }

    @PostMapping("/handovers/{handoverId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Reject till handover")
    public ResponseEntity<TillHandoverResponse> rejectHandover(@PathVariable Long handoverId,
                                                               @RequestParam String reason) {
        TillHandover handover = tillService.rejectHandover(handoverId, reason);
        return ResponseEntity.ok(toHandoverResponse(handover));
    }

    @GetMapping("/{id}/handovers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get handover history for till")
    public ResponseEntity<List<TillHandoverResponse>> getHandoverHistory(@PathVariable Long id) {
        List<TillHandover> handovers = tillService.getHandoverHistory(id);
        return ResponseEntity.ok(handovers.stream()
            .map(this::toHandoverResponse)
            .collect(Collectors.toList()));
    }

    @GetMapping("/handovers/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all pending handovers")
    public ResponseEntity<List<TillHandoverResponse>> getPendingHandovers() {
        List<TillHandover> handovers = tillService.getPendingHandovers();
        return ResponseEntity.ok(handovers.stream()
            .map(this::toHandoverResponse)
            .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    @Operation(summary = "Delete till")
    public ResponseEntity<Void> deleteTill(@PathVariable Long id) {
        tillService.deleteTill(id);
        return ResponseEntity.noContent().build();
    }

    private TillResponse toResponse(Till till) {
        return TillResponse.builder()
            .id(till.getId())
            .name(till.getName())
            .description(till.getDescription())
            .fulfillmentCenterId(till.getFulfillmentCenterId())
            .restaurantId(till.getRestaurantId())
            .status(till.getStatus())
            .openingBalance(till.getOpeningBalance())
            .currentBalance(till.getCurrentBalance())
            .expectedBalance(till.getExpectedBalance())
            .variance(till.getVariance())
            .openedAt(till.getOpenedAt())
            .closedAt(till.getClosedAt())
            .currentUserId(till.getCurrentUserId())
            .currentUserName(till.getCurrentUserName())
            .createdAt(till.getCreatedAt())
            .updatedAt(till.getUpdatedAt())
            .build();
    }

    private TillHandoverResponse toHandoverResponse(TillHandover handover) {
        return TillHandoverResponse.builder()
            .id(handover.getId())
            .tillId(handover.getTillId())
            .fromUserId(handover.getFromUserId())
            .fromUserName(handover.getFromUserName())
            .toUserId(handover.getToUserId())
            .toUserName(handover.getToUserName())
            .expectedBalance(handover.getExpectedBalance())
            .actualBalance(handover.getActualBalance())
            .variance(handover.getVariance())
            .status(handover.getStatus())
            .notes(handover.getNotes())
            .rejectionReason(handover.getRejectionReason())
            .handoverDate(handover.getHandoverDate())
            .approvedAt(handover.getApprovedAt())
            .approvedBy(handover.getApprovedBy())
            .build();
    }
}
