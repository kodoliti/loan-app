package com.finance.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.List;


/**
 * @author Artur Wojcik
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories("com.finance.domain.repository")
@EntityScan("com.finance.domain.model")
@ComponentScan({"com.finance"})
@PropertySource("classpath:application.properties")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        logger.info("Spring Boot beans :");
        List<String> beansName = Arrays.asList(ctx.getBeanDefinitionNames());
        beansName.stream().sorted().forEach((x) -> logger.info(x));
    }
}
