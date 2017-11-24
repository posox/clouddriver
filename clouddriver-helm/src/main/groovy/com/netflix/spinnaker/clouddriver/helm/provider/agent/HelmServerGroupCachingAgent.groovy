package com.netflix.spinnaker.clouddriver.helm.provider.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.frigga.Names
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.cats.agent.AgentDataType
import com.netflix.spinnaker.cats.agent.CacheResult
import com.netflix.spinnaker.cats.agent.DefaultCacheResult
import com.netflix.spinnaker.cats.provider.ProviderCache
import com.netflix.spinnaker.clouddriver.helm.cache.Keys
import com.netflix.spinnaker.clouddriver.helm.client.HelmRelease
import com.netflix.spinnaker.clouddriver.helm.model.HelmServerGroup
import com.netflix.spinnaker.clouddriver.helm.provider.view.MutableCacheData
import com.netflix.spinnaker.clouddriver.helm.security.HelmNamedAccountCredentials
import groovy.util.logging.Slf4j

import static com.netflix.spinnaker.cats.agent.AgentDataType.Authority.AUTHORITATIVE

@Slf4j
class HelmServerGroupCachingAgent extends AbstractHelmCachingAgent {
  final String category = "serverGroup"

  static final Set<AgentDataType> types = Collections.unmodifiableSet([
    AUTHORITATIVE.forType(Keys.Namespace.SERVER_GROUPS.ns),
  ] as Set)

  String agentType = "${accountName}/${HelmServerGroupCachingAgent.simpleName}"

  HelmServerGroupCachingAgent(String accountName,
                              HelmNamedAccountCredentials credentials,
                              ObjectMapper objectMapper,
                              Registry registry) {
    super(accountName, objectMapper, credentials)
  }

  @Override
  String getSimpleName() {
    HelmServerGroupCachingAgent.simpleName
  }

  @Override
  Collection<AgentDataType> getProvidedDataTypes() {
    types
  }

  @Override
  CacheResult loadData(ProviderCache providerCache) {
    log.info("Loading releases in ${agentType}")
    def releases = credentials.credentials.client.listReleases()

    Map<String, MutableCacheData> cachedServerGroups = MutableCacheData.mutableCacheMap()
    Map<String, MutableCacheData> cachedClusters = MutableCacheData.mutableCacheMap()
    Map<String, MutableCacheData> cachedApplications = MutableCacheData.mutableCacheMap()

    for (HelmRelease release : releases) {
      def serverGroupName = release.name
      def names = Names.parseName(serverGroupName)
      def applicationName = names.app
      def clusterName = names.cluster
      def namespace = release.namespace

      def serverGroupKey = Keys.getServerGroupKey(accountName, namespace, serverGroupName)
      def clusterKey = Keys.getClusterKey(accountName, applicationName, serverGroupName)
      def applicationKey = Keys.getApplicationKey(applicationName)

      cachedApplications[applicationKey].with {
        attributes.name = applicationName
        relationships[Keys.Namespace.CLUSTERS.ns].add(clusterKey)
        relationships[Keys.Namespace.SERVER_GROUPS.ns].add(serverGroupKey)
      }

      cachedClusters[clusterKey].with {
        attributes.name = clusterName
        relationships[Keys.Namespace.APPLICATIONS.ns].add(applicationKey)
        relationships[Keys.Namespace.SERVER_GROUPS.ns].add(serverGroupKey)
      }

      cachedServerGroups[serverGroupKey].with {
        attributes.name = serverGroupName
        attributes.release = release
        attributes.serverGroup = new HelmServerGroup(
          name: serverGroupName,
          region: namespace,
          account: accountName,
          chart: release.chart,
          status: release.status
        )
        relationships[Keys.Namespace.APPLICATIONS.ns].add(applicationKey)
        relationships[Keys.Namespace.CLUSTERS.ns].add(clusterKey)
      }
    }

    log.info("Caching ${cachedApplications.size()} applications in ${agentType}")
    log.info("Caching ${cachedClusters.size()} clusters in ${agentType}")
    log.info("Caching ${cachedServerGroups.size()} serverGroups in ${agentType}")

    new DefaultCacheResult([
      (Keys.Namespace.SERVER_GROUPS.ns): cachedServerGroups.values(),
      (Keys.Namespace.CLUSTERS.ns): cachedClusters.values(),
      (Keys.Namespace.APPLICATIONS.ns): cachedApplications.values(),
    ])
  }
}
