package io.github.knes1.todo;

import io.github.knes1.todo.util.HibernateStatisticsInterceptor;
import io.github.knes1.todo.util.RequestStatisticsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author knesek
 * Created on: 07/07/15
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {


	public static void main(String[] args) {
		start();
	}

	public static void start() {
		SpringApplication.run(Application.class);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder factory, DataSource dataSource,
			JpaProperties properties) {
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.putAll(properties.getHibernateProperties(dataSource));
		jpaProperties.put("hibernate.ejb.interceptor", hibernateInterceptor());
		return factory.dataSource(dataSource).packages("io.github.knes1.todo.model")
				.properties(jpaProperties).build();
	}

	@Bean
	public HibernateStatisticsInterceptor hibernateInterceptor() {
		return new HibernateStatisticsInterceptor();
	}

	@Configuration
	public static class WebApplicationConfig extends WebMvcConfigurerAdapter {

		@Autowired
		RequestStatisticsInterceptor requestStatisticsInterceptor;

		@Bean
		public RequestStatisticsInterceptor requestStatisticsInterceptor() {
			return new RequestStatisticsInterceptor();
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(requestStatisticsInterceptor).addPathPatterns("/**");
		}
	}

}
