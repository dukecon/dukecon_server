package org.dukecon

import flex.messaging.MessageBroker
import flex.messaging.io.PropertyProxyRegistry
import flex.messaging.io.SerializationContext
import org.dukecon.server.utils.LocalDateTimePropertyProxy
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.flex.messaging.MessageTemplate
import org.springframework.web.filter.ShallowEtagHeaderFilter

import javax.annotation.PostConstruct
import javax.servlet.Filter
import java.time.LocalDateTime

@SpringBootApplication
@ComponentScan("org.dukecon.server")
@EnableCircuitBreaker
@EnableAutoConfiguration
class DukeConServerApplication {

    static void main(String[] args) {
        SpringApplication.run DukeConServerApplication, args
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    @Profile("postgresql-test")
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                flyway.clean();
                flyway.migrate();
            }
        };

        return strategy;
    }

    /**
     * MessageTemplate is a little helper to send messages to any subscribed blazeds clients.
     * @param beanFactory The Spring BeanFactory used to construct new messages
     * @param messageBroker The MessageBroker instance used to send messages
     * @return A new instance of a MessageTemplate
     */
    @Bean
    public MessageTemplate messageTemplate(BeanFactory beanFactory, MessageBroker messageBroker) {
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setBeanFactory(beanFactory);
        messageTemplate.setMessageBroker(messageBroker);
        return messageTemplate;
    }

    /**
     * Fine tune the settings of the BlazeDS serialization.
     */
    @PostConstruct
    public void configureSerializationContext() {
        //ThreadLocal SerializationContent
        SerializationContext serializationContext = SerializationContext.getSerializationContext();
        serializationContext.enableSmallMessages = true;
        serializationContext.instantiateTypes = true;
        //use _remoteClass field
        serializationContext.supportRemoteClass = true;
        //false  Legacy Flex 1.5 behavior was to return a java.util.Collection for Array
        //true New Flex 2+ behavior is to return Object[] for AS3 Array
        serializationContext.legacyCollection = true;

        serializationContext.legacyMap = false;
        //false Legacy flash.xml.XMLDocument Type
        //true New E4X XML Type
        serializationContext.legacyXMLDocument = false;

        //determines whether the constructed Document is name-space aware
        serializationContext.legacyXMLNamespaces = false;
        serializationContext.legacyThrowable = false;
        serializationContext.legacyBigNumbers = false;

        serializationContext.restoreReferences = false;
        serializationContext.logPropertyErrors = false;
        serializationContext.ignorePropertyErrors = true;

        // Register a property proxy that allows serialization of LocalDateTime objects.
        PropertyProxyRegistry.getRegistry().register(
                LocalDateTime.class, new LocalDateTimePropertyProxy()
        );
    }

}


