package org.dukecon.server.adapter

import org.dukecon.server.conference.ConferencesConfiguration
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.json.YamlJsonParser
import org.springframework.core.env.Environment

/**
 * Reads all conferences from configuration file and generates an #ConferenceDataProvider for each.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DataProviderLoader implements BeanDefinitionRegistryPostProcessor {

    private ConferencesConfiguration configuration = new ConferencesConfiguration()

    DataProviderLoader(Environment springEnvironment) {
        configuration.conferences.addAll(ConferencesConfiguration.fromFile('conferences.yml')?.conferences)
    }

    @Override
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        configuration.conferences.each { ConferencesConfiguration.Conference config ->
            if (config.backupUri) {
                BeanDefinitionBuilder builderDataProviderRemote = BeanDefinitionBuilder.genericBeanDefinition(WebResourceDataProviderRemote)
                def dataExtractor = Class.forName(config.extractorClass).newInstance(config.id, this.class.getResourceAsStream("/${config.talksUri}"), config.startDate, config.name, config.url)
                builderDataProviderRemote.addConstructorArgValue(dataExtractor)
                builderDataProviderRemote.addConstructorArgValue(this.class.getResourceAsStream("/${config.backupUri}"))
                beanDefinitionRegistry.registerBeanDefinition("${config.name} dataprovider remote", builderDataProviderRemote.beanDefinition)

                BeanDefinitionBuilder builderDataProvider = BeanDefinitionBuilder.genericBeanDefinition(WebResourceDataProvider)
                builderDataProvider.addConstructorArgReference("${config.name} dataprovider remote")
                beanDefinitionRegistry.registerBeanDefinition("${config.name} dataprovider", builderDataProvider.beanDefinition)

                BeanDefinitionBuilder builderHealthCheck = BeanDefinitionBuilder.genericBeanDefinition(WebResourceDataProviderHealthIndicator)
                builderHealthCheck.addConstructorArgReference("${config.name} dataprovider")
                builderHealthCheck.addConstructorArgReference("${config.name} dataprovider remote")
                beanDefinitionRegistry.registerBeanDefinition("${config.name} dataprovider health indicator", builderHealthCheck.beanDefinition)
            } else {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LocalResourceDataProvider)
                def dataExtractor = Class.forName(config.extractorClass).newInstance(config.id, this.class.getResourceAsStream("/${config.talksUri}"), config.startDate, config.name, config.url)
                builder.addConstructorArgValue(dataExtractor)
                beanDefinitionRegistry.registerBeanDefinition("${config.name} dataprovider", builder.beanDefinition)
            }
        }
    }

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
