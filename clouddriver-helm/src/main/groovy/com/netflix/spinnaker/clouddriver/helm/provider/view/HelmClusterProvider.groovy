package com.netflix.spinnaker.clouddriver.helm.provider.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.frigga.Names
import com.netflix.spinnaker.cats.cache.Cache
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.cats.cache.RelationshipCacheFilter
import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.helm.cache.Keys
import com.netflix.spinnaker.clouddriver.helm.model.HelmCluster
import com.netflix.spinnaker.clouddriver.helm.model.HelmServerGroup
import com.netflix.spinnaker.clouddriver.model.Application
import com.netflix.spinnaker.clouddriver.model.ClusterProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HelmClusterProvider implements ClusterProvider<HelmCluster> {
  @Autowired
  Cache cacheView

  @Autowired
  ObjectMapper objectMapper

  @Autowired
  HelmApplicationProvider helmApplicationProvider

  @Override
  Map<String, Set<HelmCluster>> getClusters() {
    Collection<CacheData> clusterData = cacheView.getAll(Keys.Namespace.CLUSTERS.ns)
    translateClusters(clusterData, true).groupBy { it.accountName } as Map<String, Set<HelmCluster>>
  }

  @Override
  Map<String, Set<HelmCluster>> getClusterSummaries(String application) {
    translateClusters(getClusterData(application), false)?.groupBy { it.accountName } as Map<String, Set<HelmCluster>>
  }

  @Override
  Map<String, Set<HelmCluster>> getClusterDetails(String application) {
    translateClusters(getClusterData(application), false)?.groupBy { it.accountName } as Map<String, Set<HelmCluster>>
  }

  @Override
  Set<HelmCluster> getClusters(String applicationName, String account) {
    CacheData application = cacheView.get(Keys.Namespace.APPLICATIONS.ns,
                                          Keys.getApplicationKey(applicationName),
                                          RelationshipCacheFilter.include(Keys.Namespace.CLUSTERS.ns))
    if (!application) {
      return []
    }

    Collection<String> clusterKeys = application.relationships[Keys.Namespace.CLUSTERS.ns]
      .findAll { Keys.parse(it).account == account }
    Collection<CacheData> clusterData = cacheView.getAll(Keys.Namespace.CLUSTERS.ns, clusterKeys)

    translateClusters(clusterData, true)
  }

  @Override
  HelmCluster getCluster(String applicationName, String account, String clusterName) {
    getCluster(applicationName, account, clusterName, true)
  }

  @Override
  HelmCluster getCluster(String application, String account, String name, boolean includeDetails) {
    List<CacheData> clusterData =
      [cacheView.get(Keys.Namespace.CLUSTERS.ns, Keys.getClusterKey(account, application, name))] - null

    clusterData ? translateClusters(clusterData, includeDetails).head() : null
  }

  @Override
  HelmServerGroup getServerGroup(String account, String region, String serverGroupName) {

    String serverGroupKey = Keys.getServerGroupKey(account, region, serverGroupName)
    CacheData serverGroupData = cacheView.get(Keys.Namespace.SERVER_GROUPS.ns, serverGroupKey)

    if (!serverGroupData) {
      return null
    }

    HelmProviderUtils.serverGroupFromCacheData(objectMapper, serverGroupData)
  }

  @Override
  String getCloudProviderId() {
    HelmCloudProvider.ID
  }

  @Override
  boolean supportsMinimalClusters() {
    return false
  }

  Collection<HelmCluster> translateClusters(Collection<CacheData> clusterData, boolean includeDetails) {
    if (!clusterData) {
      return []
    }

    // TODO: implement details
    Map<String, Set<HelmServerGroup>> serverGroups

    Collection<HelmCluster> clusters = clusterData.collect { CacheData clusterDataEntry ->
      def clusterKey = Keys.parse(clusterDataEntry.id)

      def cluster = new HelmCluster()
      cluster.accountName = clusterKey.account
      cluster.name = clusterKey.name
      cluster.serverGroups = clusterDataEntry.relationships[Keys.Namespace.SERVER_GROUPS.ns]?.collect { serverGroupKey ->
        def parts = Keys.parse(serverGroupKey)
        new HelmServerGroup(name: parts.name, account: parts.account, region: parts.region)
      }
      cluster
    }
    clusters
  }

  Map<String, Set<HelmServerGroup>> translateServerGroups(Collection<CacheData> serverGroupData) {
    return serverGroupData
      .inject([:].withDefault { [] }, { Map<String, Set<HelmServerGroup>> acc, CacheData cacheData ->
      def serverGroup = HelmProviderUtils.serverGroupFromCacheData(objectMapper, cacheData)
      acc[Names.parseName(serverGroup.name).cluster].add(serverGroup)
      acc
    })
  }

  Set<CacheData> getClusterData(String applicationName) {
    Application application = helmApplicationProvider.getApplication(applicationName)
    def clusterKeys = []
    application?.clusterNames?.each { String accountName, Set<String> clusterNames ->
      clusterKeys.addAll(clusterNames.collect { clusterName ->
        Keys.getClusterKey(accountName, applicationName, clusterName)
      })
    }

    cacheView.getAll(Keys.Namespace.CLUSTERS.ns, clusterKeys)
  }
}
