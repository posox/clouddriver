package com.netflix.spinnaker.clouddriver.helm.provider.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.cats.agent.AccountAware
import com.netflix.spinnaker.cats.agent.CachingAgent
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.helm.provider.HelmProvider
import com.netflix.spinnaker.clouddriver.helm.security.HelmNamedAccountCredentials

abstract class AbstractHelmCachingAgent implements CachingAgent, AccountAware {
  final String accountName
  final String providerName = HelmProvider.PROVIDER_NAME
  final HelmCloudProvider helmCloudProvider = new HelmCloudProvider()
  final ObjectMapper objectMapper
  final HelmNamedAccountCredentials credentials

  AbstractHelmCachingAgent(String accountName,
                           ObjectMapper objectMapper,
                           HelmNamedAccountCredentials credentials) {
    this.accountName = accountName
    this.objectMapper = objectMapper
    this.credentials = credentials
  }

  static void cache(Map<String, List<CacheData>> cacheResults,
                    String cacheNamespace,
                    Map<String, CacheData> cacheDataById) {
    cacheResults[cacheNamespace].each {
      def existingCacheData = cacheDataById[it.id]
      if (existingCacheData) {
        existingCacheData.attributes.putAll(it.attributes)
        it.relationships.each { String relationshipName, Collection<String> relationships ->
          existingCacheData.relationships[relationshipName].addAll(relationships)
        }
      } else {
        cacheDataById[it.id] = it
      }
    }
  }

  @Override
  public String getAgentType() {
    return String.format("%s/%s[%d/%d]", accountName, this.getClass().getSimpleName(), agentIndex + 1, agentCount);
  }

  abstract String getSimpleName()
}
