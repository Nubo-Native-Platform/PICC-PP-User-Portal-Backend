package com.nnp.dashboard.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.nnp.dashboard.repo.apiecosystem",   // repos for apiecosystem DB
        entityManagerFactoryRef = "apiecosystemEntityManager",
        transactionManagerRef = "apiecosystemTransactionManager"
)
public class ApiecosystemDbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.apiecosystem")
    public DataSourceProperties apiecosystemDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource apiecosystemDataSource() {
        return apiecosystemDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "apiecosystemEntityManager")
    public LocalContainerEntityManagerFactoryBean apiecosystemEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(apiecosystemDataSource())
                .packages(
                        "com.nnp.dashboard.model.apiecosystem" // entities for apiecosystem DB
                )
                .persistenceUnit("apiecosystem")
                .build();
    }

    @Bean(name = "apiecosystemTransactionManager")
    public PlatformTransactionManager apiecosystemTransactionManager(
            @Qualifier("apiecosystemEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
