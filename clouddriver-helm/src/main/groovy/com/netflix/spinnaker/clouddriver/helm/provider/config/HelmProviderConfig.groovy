package com.netflix.spinnaker.clouddriver.helm.provider.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.cats.agent.Agent
import com.netflix.spinnaker.cats.provider.ProviderSynchronizerTypeWrapper
import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.helm.provider.HelmProvider
import com.netflix.spinnaker.clouddriver.helm.provider.agent.HelmServerGroupCachingAgent
import com.netflix.spinnaker.clouddriver.helm.security.HelmNamedAccountCredentials
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsRepository
import com.netflix.spinnaker.clouddriver.security.ProviderUtils
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Scope

import java.util.concurrent.ConcurrentHashMap

@Configuration
@Slf4j
class HelmProviderConfig {
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Bean
  HelmProviderSynchronizer synchronizeHelmProvider(HelmProvider helmProvider,
                                                   AccountCredentialsRepository accountCredentialsRepository,
                                                   ObjectMapper objectMapper,
                                                   Registry registry) {
    def scheduledAccounts = ProviderUtils.getScheduledAccounts(helmProvider)
    def allAccounts = ProviderUtils.buildThreadSafeSetOfAccounts(accountCredentialsRepository,
      HelmNamedAccountCredentials)

    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    def newlyAddedAgents = []
    allAccounts.each { HelmNamedAccountCredentials credentials ->
      if (!scheduledAccounts.contains(credentials.name)) {
        newlyAddedAgents << new HelmServerGroupCachingAgent(credentials.name,
          credentials,
          objectMapper,
          registry)
      }
      log.info("Adding ${newlyAddedAgents.size()} agents for account ${credentials.name}")
    }


    if (!newlyAddedAgents.isEmpty()) {
      helmProvider.agents.addAll(newlyAddedAgents)
    }

    new HelmProviderSynchronizer()
  }

  class HelmProviderSynchronizer { }

  @Bean
  @DependsOn('helmNamedAccountCredentials')
  HelmProvider helmProvider(HelmCloudProvider helmCloudProvider,
                            AccountCredentialsRepository accountCredentialsRepository,
                            ObjectMapper objectMapper,
                            Registry registry) {
    def helmProvider = new HelmProvider(helmCloudProvider,
                                        Collections.newSetFromMap(new ConcurrentHashMap<Agent, Boolean>()))

    synchronizeHelmProvider(helmProvider,
                            accountCredentialsRepository,
                            objectMapper,
                            registry)

    helmProvider
  }

  class HelmProviderSynchronizerTypeWrapper implements ProviderSynchronizerTypeWrapper {

    @Override
    Class getSynchronizerType() {
      HelmProviderSynchronizer
    }
  }

  @Bean
  HelmProviderSynchronizerTypeWrapper helmProviderSynchronizerTypeWrapper() {
    new HelmProviderSynchronizerTypeWrapper()
  }

}
