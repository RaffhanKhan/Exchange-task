package com.exchange.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.HashMap;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "com.exchange.currency", entityManagerFactoryRef = "exchangeEntityManager", transactionManagerRef = "platformTransactionManager")
public class CurrencyApplication {

	private final Environment env;

	@Bean   // used concept of @Configuration in @SpringBootApplication so no need to configure @Configuration
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public PlatformTransactionManager platformTransactionManager(){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(exchangeEntityManager().getObject());
		return transactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean exchangeEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(exchangeDataSource());

		em.setPackagesToScan("com.exchange.currency");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		HashMap<String, Object> props = new HashMap<>();
		props.put("hibernate.dialect", env.getProperty("exchange.hibernate.dialect"));
		props.put("hibernate.show_sql", env.getProperty("exchange.hibernate.show.sql"));
		em.setJpaPropertyMap(props);

		return em;
	}

	@Bean(destroyMethod = "")
	public DataSource exchangeDataSource(){
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(env.getProperty("exchange.jdbc.classname"));
		driverManagerDataSource.setUsername(env.getProperty("exchange.jdbc.username"));
		driverManagerDataSource.setPassword(env.getProperty("exchange.jdbc.password"));
		driverManagerDataSource.setUrl(env.getProperty("exchange.jdbc.url"));
		return driverManagerDataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(CurrencyApplication.class, args);
	}

}
