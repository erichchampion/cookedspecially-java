package com.cookedspecially.reportingservice.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Database configuration with read replica support for reporting queries.
 * Primary datasource is used for write operations (scheduled reports, execution history).
 * Read replica datasource is used for read-only reporting queries.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.cookedspecially.reportingservice.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class DatabaseConfig {

    /**
     * Primary datasource properties (for writes).
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Read replica datasource properties (for reporting queries).
     */
    @Bean
    @ConfigurationProperties("spring.datasource.replica")
    public DataSourceProperties replicaDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Primary datasource bean (for writes).
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    /**
     * Read replica datasource bean (for reporting queries).
     * This datasource should point to a MySQL read replica to avoid
     * impacting production database performance.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.replica.hikari")
    public DataSource replicaDataSource() {
        DataSourceProperties properties = replicaDataSourceProperties();

        // If replica URL is not configured, fall back to primary datasource
        if (properties.getUrl() == null || properties.getUrl().isEmpty()) {
            return primaryDataSource();
        }

        return properties
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    /**
     * Entity manager factory using primary datasource.
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("primaryDataSource") DataSource primaryDataSource
    ) {
        return builder
            .dataSource(primaryDataSource)
            .packages("com.cookedspecially.reportingservice.domain")
            .persistenceUnit("primary")
            .build();
    }

    /**
     * Transaction manager for primary datasource.
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
        @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * Routing datasource that switches between primary and replica based on transaction context.
     * For read-only transactions, it routes to replica datasource.
     * For write transactions, it routes to primary datasource.
     */
    @Bean
    public DataSource routingDataSource(
        @Qualifier("primaryDataSource") DataSource primaryDataSource,
        @Qualifier("replicaDataSource") DataSource replicaDataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);

        var dataSourceMap = new java.util.HashMap<Object, Object>();
        dataSourceMap.put("primary", primaryDataSource);
        dataSourceMap.put("replica", replicaDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);

        return routingDataSource;
    }
}
