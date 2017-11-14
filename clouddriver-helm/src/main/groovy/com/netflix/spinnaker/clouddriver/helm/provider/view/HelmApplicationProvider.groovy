package com.netflix.spinnaker.clouddriver.helm.provider.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.cats.cache.Cache
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.cats.cache.RelationshipCacheFilter
import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.helm.cache.Keys
import com.netflix.spinnaker.clouddriver.helm.model.HelmApplication
import com.netflix.spinnaker.clouddriver.model.Application
import com.netflix.spinnaker.clouddriver.model.ApplicationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.netflix.spinnaker.clouddriver.helm.cache.Keys.Namespace.APPLICATIONS
import static com.netflix.spinnaker.clouddriver.helm.cache.Keys.Namespace.CLUSTERS

@Component
class HelmApplicationProvider implements ApplicationProvider {
  private final HelmCloudProvider helmCloudProvider
  private final Cache cacheView
  private final ObjectMapper objectMapper

  @Autowired
  HelmApplicationProvider(HelmCloudProvider helmCloudProvider, Cache cacheView, ObjectMapper objectMapper) {
    this.helmCloudProvider = helmCloudProvider
    this.cacheView = cacheView
    this.objectMapper = objectMapper
  }

  @Override
  Set<? extends Application> getApplications(boolean expand) {
    def relationships = expand ? RelationshipCacheFilter.include(CLUSTERS.ns) : RelationshipCacheFilter.none()
    Collection<CacheData> applications = cacheView.getAll(APPLICATIONS.ns, cacheView.filterIdentifiers(APPLICATIONS.ns, "${HelmCloudProvider.ID}:*"), relationships)
    applications.collect(this.&translate)
  }

  @Override
  Application getApplication(String name) {
    translate(cacheView.get(APPLICATIONS.ns, Keys.getApplicationKey(name)))
  }

  Application translate(CacheData cacheData) {
    if (cacheData == null) {
      return null
    }

    String name = Keys.parse(cacheData.id).application
    Map<String, String> attributes = objectMapper.convertValue(cacheData.attributes, HelmApplication.ATTRIBUTES)
    Map<String, Set<String>> clusterNames = [:].withDefault { new HashSet<String>() }
    for (String clusterId : cacheData.relationships[CLUSTERS.ns]) {
      Map<String, String> cluster = Keys.parse(clusterId)
      if (cluster.account && cluster.name) {
        clusterNames[cluster.account].add(cluster.name)
      }
    }

    new HelmApplication(name, attributes, clusterNames)
  }
}
