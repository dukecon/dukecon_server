package org.dukecon

import org.dukecon.server.core.MyShallowEtagHeaderFilter
import org.dukecon.server.repositories.DataProviderLoader
import org.dukecon.server.conference.ConferencesConfigurationServiceImpl
import org.flywaydb.core.Flyway
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile

import javax.servlet.Filter

@SpringBootApplication
@ComponentScan("org.dukecon.server")
@EnableCircuitBreaker
class DukeConServerApplication {

    static class DataProviderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext ctx) {
            ctx.addBeanFactoryPostProcessor(new DataProviderLoader(new ConferencesConfigurationServiceImpl(ctx.getEnvironment())))
        }
    }

    static void main(String[] args) {
        def application = new SpringApplication(DukeConServerApplication)
        application.addInitializers(new DataProviderInitializer())
        application.run(args)
    }

    @Bean
    Filter shallowEtagHeaderFilter() {
        return new MyShallowEtagHeaderFilter()
    }

    @Bean
    @Profile("postgresql-test")
    FlywayMigrationStrategy cleanMigrateStrategy() {
        FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
            @Override
            void migrate(Flyway flyway) {
                flyway.clean()
                flyway.migrate()
            }
        }

        return strategy
    }

}


