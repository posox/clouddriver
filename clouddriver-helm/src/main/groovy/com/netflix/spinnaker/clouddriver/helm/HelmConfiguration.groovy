package com.netflix.spinnaker.clouddriver.helm

import com.netflix.spinnaker.clouddriver.helm.config.HelmConfigurationProperties
import com.netflix.spinnaker.clouddriver.helm.health.HelmHealthIndicator
import com.netflix.spinnaker.clouddriver.helm.security.HelmNamedAccountCredentialsInitializer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableConfigurationProperties
@EnableScheduling
@ConditionalOnProperty('helm.enabled')
@ComponentScan(["com.netflix.spinnaker.clouddriver.helm"])
@Import([HelmNamedAccountCredentialsInitializer])
class HelmConfiguration {
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Bean
  @ConfigurationProperties("helm")
  HelmConfigurationProperties helmConfigurationProperties() {
    new HelmConfigurationProperties()
  }

  @Bean
  HelmHealthIndicator helmHealthIndicator() {
    new HelmHealthIndicator()
  }
}
