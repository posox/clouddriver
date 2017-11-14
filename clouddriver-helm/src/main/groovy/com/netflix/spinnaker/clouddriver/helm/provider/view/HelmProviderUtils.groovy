package com.netflix.spinnaker.clouddriver.helm.provider.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.clouddriver.helm.model.HelmServerGroup

class HelmProviderUtils {
  static HelmServerGroup serverGroupFromCacheData(ObjectMapper objectMapper, CacheData cacheData) {
    objectMapper.convertValue(cacheData.attributes.serverGroup, HelmServerGroup)
  }
}
