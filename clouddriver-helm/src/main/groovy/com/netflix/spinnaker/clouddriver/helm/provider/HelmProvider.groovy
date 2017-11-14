package com.netflix.spinnaker.clouddriver.helm.provider

import com.netflix.spinnaker.cats.agent.Agent
import com.netflix.spinnaker.cats.agent.AgentSchedulerAware
import com.netflix.spinnaker.clouddriver.cache.SearchableProvider
import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.helm.cache.Keys

class HelmProvider extends AgentSchedulerAware implements SearchableProvider {
  public static final String PROVIDER_NAME = HelmProvider.name

  final Map<String, String> urlMappingTemplates = Collections.emptyMap()

  final Collection<Agent> agents
  final HelmCloudProvider cloudProvider

  HelmProvider(HelmCloudProvider cloudProvider, Collection<Agent> agents) {
    this.cloudProvider = cloudProvider
    this.agents = agents
  }

  final Set<String> defaultCaches = [
    Keys.Namespace.SERVER_GROUPS.ns,
  ].asImmutable()

  final Map<SearchableProvider.SearchableResource, SearchableProvider.SearchResultHydrator> searchResultHydrators = Collections.emptyMap()

  @Override
  Map<String, String> parseKey(String key) {
    Keys.parse(key)
  }

  @Override
  String getProviderName() {
    PROVIDER_NAME
  }
}
