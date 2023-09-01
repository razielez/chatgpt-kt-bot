package com.razielez.chatgpt.app

import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

open class PropertiesBeanFactory(
    private val config: String,
) : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val properties = Properties()
        properties.load(Files.newInputStream(Path.of(config)))
        val propertySource = MapConfigurationPropertySource(properties)
        val binder = Binder(propertySource)
        // beanFactory.registerSingleton("", "")
    }

}