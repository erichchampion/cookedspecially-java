package com.cookedspecially.reportingservice.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Routing datasource that automatically routes queries to read replica
 * for read-only transactions and to primary datasource for write transactions.
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // Route to replica if transaction is read-only
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return isReadOnly ? "replica" : "primary";
    }
}
