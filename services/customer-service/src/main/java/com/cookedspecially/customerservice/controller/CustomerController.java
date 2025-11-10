package com.cookedspecially.customerservice.controller;

import com.cookedspecially.customerservice.domain.CustomerStatus;
import com.cookedspecially.customerservice.dto.CreateCustomerRequest;
import com.cookedspecially.customerservice.dto.CustomerResponse;
import com.cookedspecially.customerservice.dto.UpdateCustomerRequest;
import com.cookedspecially.customerservice.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Customer Controller - REST endpoints for customer management
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Customer management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Create a new customer
     */
    @PostMapping
    @Operation(summary = "Create customer", description = "Register a new customer account")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        logger.info("Creating customer with email: {}", request.getEmail());
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get customer profile (current user)
     */
    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Get current customer's profile")
    public ResponseEntity<CustomerResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        logger.info("Fetching profile for Cognito sub: {}", cognitoSub);
        CustomerResponse response = customerService.getCustomerByCognitoSub(cognitoSub);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Get customer by ID", description = "Get customer details by ID (admin/owner only)")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        logger.info("Fetching customer: {}", id);
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.claims['customer_id'] or hasRole('ADMIN')")
    @Operation(summary = "Update customer", description = "Update customer profile")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        logger.info("Updating customer: {}", id);
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete customer account
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.claims['customer_id'] or hasRole('ADMIN')")
    @Operation(summary = "Delete customer", description = "Delete customer account (soft delete)")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        logger.info("Deleting customer: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verify customer email
     */
    @PostMapping("/{id}/verify-email")
    @Operation(summary = "Verify email", description = "Verify customer email address")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long id) {
        logger.info("Verifying email for customer: {}", id);
        customerService.verifyEmail(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Update customer status (admin only)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update customer status", description = "Update customer status (admin only)")
    public ResponseEntity<CustomerResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam CustomerStatus status
    ) {
        logger.info("Updating status for customer {}: {}", id, status);
        CustomerResponse response = customerService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Add loyalty points (internal endpoint)
     */
    @PostMapping("/{id}/loyalty-points")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @Operation(summary = "Add loyalty points", description = "Add loyalty points to customer (internal)")
    public ResponseEntity<Void> addLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam int points
    ) {
        logger.info("Adding {} loyalty points to customer: {}", points, id);
        customerService.addLoyaltyPoints(id, points);
        return ResponseEntity.ok().build();
    }

    /**
     * Add account credit (internal endpoint)
     */
    @PostMapping("/{id}/credit/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @Operation(summary = "Add credit", description = "Add credit to customer account (internal)")
    public ResponseEntity<Void> addCredit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        logger.info("Adding {} credit to customer: {}", amount, id);
        customerService.addCredit(id, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * Deduct account credit (internal endpoint)
     */
    @PostMapping("/{id}/credit/deduct")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @Operation(summary = "Deduct credit", description = "Deduct credit from customer account (internal)")
    public ResponseEntity<Void> deductCredit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        logger.info("Deducting {} credit from customer: {}", amount, id);
        customerService.deductCredit(id, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * Update order statistics (internal endpoint)
     */
    @PostMapping("/{id}/order-stats")
    @PreAuthorize("hasRole('SYSTEM')")
    @Operation(summary = "Update order stats", description = "Update customer order statistics (internal)")
    public ResponseEntity<Void> updateOrderStats(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        logger.info("Updating order stats for customer: {}, amount: {}", id, amount);
        customerService.updateOrderStats(id, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all customers (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all customers", description = "Get all customers (admin only)")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        logger.info("Fetching all customers, page: {}", pageable.getPageNumber());
        Page<CustomerResponse> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers (admin only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Search customers", description = "Search customers by name or email")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @RequestParam String query,
            Pageable pageable
    ) {
        logger.info("Searching customers: {}", query);
        Page<CustomerResponse> customers = customerService.searchCustomers(query, pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by status (admin only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get customers by status", description = "Get customers by status (admin only)")
    public ResponseEntity<Page<CustomerResponse>> getCustomersByStatus(
            @PathVariable CustomerStatus status,
            Pageable pageable
    ) {
        logger.info("Fetching customers with status: {}", status);
        Page<CustomerResponse> customers = customerService.getCustomersByStatus(status, pageable);
        return ResponseEntity.ok(customers);
    }
}
