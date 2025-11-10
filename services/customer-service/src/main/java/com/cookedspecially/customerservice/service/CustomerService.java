package com.cookedspecially.customerservice.service;

import com.cookedspecially.customerservice.domain.Customer;
import com.cookedspecially.customerservice.domain.CustomerStatus;
import com.cookedspecially.customerservice.dto.*;
import com.cookedspecially.customerservice.exception.CustomerAlreadyExistsException;
import com.cookedspecially.customerservice.exception.CustomerNotFoundException;
import com.cookedspecially.customerservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Customer service - Business logic for customer management
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Create a new customer
     */
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        logger.info("Creating customer with email: {}", request.getEmail());

        // Check if customer already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email " + request.getEmail() + " already exists");
        }

        if (customerRepository.existsByCognitoSub(request.getCognitoSub())) {
            throw new CustomerAlreadyExistsException("Customer with Cognito ID already exists");
        }

        Customer customer = Customer.builder()
                .cognitoSub(request.getCognitoSub())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .profileImageUrl(request.getProfileImageUrl())
                .status(CustomerStatus.PENDING_VERIFICATION)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created with ID: {}", savedCustomer.getId());

        return customerMapper.toResponse(savedCustomer);
    }

    /**
     * Get customer by ID
     */
    @Cacheable(value = "customers", key = "#id")
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        logger.debug("Fetching customer by ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return customerMapper.toResponse(customer);
    }

    /**
     * Get customer by Cognito subject
     */
    @Cacheable(value = "customers", key = "'cognito:' + #cognitoSub")
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByCognitoSub(String cognitoSub) {
        logger.debug("Fetching customer by Cognito sub: {}", cognitoSub);
        Customer customer = customerRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with Cognito ID: " + cognitoSub));
        return customerMapper.toResponse(customer);
    }

    /**
     * Get customer by email
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        logger.debug("Fetching customer by email: {}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
        return customerMapper.toResponse(customer);
    }

    /**
     * Update customer
     */
    @CacheEvict(value = "customers", key = "#id")
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        logger.info("Updating customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        // Update fields if provided
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            customer.setGender(request.getGender());
        }
        if (request.getProfileImageUrl() != null) {
            customer.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getEmailNotifications() != null) {
            customer.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getSmsNotifications() != null) {
            customer.setSmsNotifications(request.getSmsNotifications());
        }
        if (request.getPushNotifications() != null) {
            customer.setPushNotifications(request.getPushNotifications());
        }
        if (request.getMarketingEmails() != null) {
            customer.setMarketingEmails(request.getMarketingEmails());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Customer updated: {}", updatedCustomer.getId());

        return customerMapper.toResponse(updatedCustomer);
    }

    /**
     * Delete customer (soft delete)
     */
    @CacheEvict(value = "customers", key = "#id")
    public void deleteCustomer(Long id) {
        logger.info("Deleting customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setStatus(CustomerStatus.DELETED);
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);

        logger.info("Customer soft-deleted: {}", id);
    }

    /**
     * Verify customer email
     */
    @CacheEvict(value = "customers", key = "#id")
    public void verifyEmail(Long id) {
        logger.info("Verifying email for customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setEmailVerified(true);
        if (customer.getStatus() == CustomerStatus.PENDING_VERIFICATION) {
            customer.setStatus(CustomerStatus.ACTIVE);
        }
        customerRepository.save(customer);

        logger.info("Email verified for customer: {}", id);
    }

    /**
     * Update customer status
     */
    @CacheEvict(value = "customers", key = "#id")
    public CustomerResponse updateStatus(Long id, CustomerStatus status) {
        logger.info("Updating status for customer: {} to {}", id, status);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setStatus(status);
        Customer updatedCustomer = customerRepository.save(customer);

        logger.info("Customer status updated: {}", id);
        return customerMapper.toResponse(updatedCustomer);
    }

    /**
     * Add loyalty points
     */
    @CacheEvict(value = "customers", key = "#id")
    public void addLoyaltyPoints(Long id, int points) {
        logger.info("Adding {} loyalty points to customer: {}", points, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.incrementLoyaltyPoints(points);
        customerRepository.save(customer);
    }

    /**
     * Add account credit
     */
    @CacheEvict(value = "customers", key = "#id")
    public void addCredit(Long id, BigDecimal amount) {
        logger.info("Adding {} credit to customer: {}", amount, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.addCredit(amount);
        customerRepository.save(customer);
    }

    /**
     * Deduct account credit
     */
    @CacheEvict(value = "customers", key = "#id")
    public void deductCredit(Long id, BigDecimal amount) {
        logger.info("Deducting {} credit from customer: {}", amount, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        if (customer.getAccountCredit().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient credit balance");
        }

        customer.deductCredit(amount);
        customerRepository.save(customer);
    }

    /**
     * Update order statistics
     */
    @CacheEvict(value = "customers", key = "#id")
    public void updateOrderStats(Long id, BigDecimal orderAmount) {
        logger.info("Updating order stats for customer: {}, amount: {}", id, orderAmount);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.incrementOrderStats(orderAmount);
        customerRepository.save(customer);
    }

    /**
     * Get all customers (paginated)
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        logger.debug("Fetching all customers, page: {}", pageable.getPageNumber());
        return customerRepository.findAllActive(pageable)
                .map(customerMapper::toResponse);
    }

    /**
     * Search customers
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String query, Pageable pageable) {
        logger.debug("Searching customers with query: {}", query);
        return customerRepository.searchCustomers(query, pageable)
                .map(customerMapper::toResponse);
    }

    /**
     * Get customers by status
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getCustomersByStatus(CustomerStatus status, Pageable pageable) {
        logger.debug("Fetching customers with status: {}", status);
        return customerRepository.findByStatus(status, pageable)
                .map(customerMapper::toResponse);
    }
}
