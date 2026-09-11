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
        basePackages = "com.nnp.dashboard.repo.devsecops",   // repos for devsecops DB
        entityManagerFactoryRef = "devsecopsEntityManager",
        transactionManagerRef = "devsecopsTransactionManager"
)
public class DevscopsDbConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.devsecops")
    public DataSourceProperties devsecopsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource devsecopsDataSource() {
        return devsecopsDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "devsecopsEntityManager")
    public LocalContainerEntityManagerFactoryBean devsecopsEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(devsecopsDataSource())
                .packages(
                        "com.nnp.dashboard.model.devsecops" // entities for devsecops DB
                )
                .persistenceUnit("devsecops")
                .build();
    }

    @Bean(name = "devsecopsTransactionManager")
    public PlatformTransactionManager devsecopsTransactionManager(
            @Qualifier("devsecopsEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
