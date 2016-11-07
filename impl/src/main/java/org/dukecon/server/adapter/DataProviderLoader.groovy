package org.dukecon.server.adapter

import org.dukecon.server.conference.ConferencesConfiguration
import org.springframework.beans.BeansException
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
                // TODO configure WebFileDataProvider
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
