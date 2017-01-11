package org.dukecon

import flex.messaging.MessageBroker
import flex.messaging.io.SerializationContext
import org.dukecon.server.adapter.DataProviderLoader
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.BeanFactory
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
import org.springframework.flex.messaging.MessageTemplate
import org.springframework.web.filter.ShallowEtagHeaderFilter

import javax.annotation.PostConstruct
import javax.servlet.Filter

@SpringBootApplication
@ComponentScan("org.dukecon.server")
@EnableCircuitBreaker
@EnableAutoConfiguration
class DukeConServerApplication {

    static class DataProviderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext ctx) {
            ctx.addBeanFactoryPostProcessor(new DataProviderLoader(ctx.getEnvironment()))
        }
    }

    static void main(String[] args) {
        def application = new SpringApplication(DukeConServerApplication)
        application.addInitializers(new DataProviderInitializer())
        application.run(args)
    }

    @Bean
    Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter()
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

    /**
     * MessageTemplate is a little helper to send messages to any subscribed blazeds clients.
     * @param beanFactory The Spring BeanFactory used to construct new messages
     * @param messageBroker The MessageBroker instance used to send messages
     * @return A new instance of a MessageTemplate
     */
    @Bean
    MessageTemplate messageTemplate(BeanFactory beanFactory, MessageBroker messageBroker) {
        MessageTemplate messageTemplate = new MessageTemplate()
        messageTemplate.setBeanFactory(beanFactory)
        messageTemplate.setMessageBroker(messageBroker)
        return messageTemplate
    }

    /**
     * Fine tune the settings of the BlazeDS serialization.
     */
    @PostConstruct
    static void configureSerializationContext() {
        //ThreadLocal SerializationContent
        SerializationContext serializationContext = SerializationContext.getSerializationContext()
        serializationContext.enableSmallMessages = true
        serializationContext.instantiateTypes = true
        //use _remoteClass field
        serializationContext.supportRemoteClass = true
        //false  Legacy Flex 1.5 behavior was to return a java.util.Collection for Array
        //true New Flex 2+ behavior is to return Object[] for AS3 Array
        serializationContext.legacyCollection = true

        serializationContext.legacyMap = false
        //false Legacy flash.xml.XMLDocument Type
        //true New E4X XML Type
        serializationContext.legacyXMLDocument = false

        //determines whether the constructed Document is name-space aware
        serializationContext.legacyXMLNamespaces = false
        serializationContext.legacyThrowable = false
        serializationContext.legacyBigNumbers = false

        serializationContext.restoreReferences = false
        serializationContext.logPropertyErrors = false
        serializationContext.ignorePropertyErrors = true
    }

}


