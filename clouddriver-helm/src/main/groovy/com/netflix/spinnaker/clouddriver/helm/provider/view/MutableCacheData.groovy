package com.netflix.spinnaker.clouddriver.helm.provider.view

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.netflix.spinnaker.cats.cache.CacheData

/* TODO(lwander) this was taken from the netflix cluster caching, and should probably be shared between all providers. */
class MutableCacheData implements CacheData {
  final String id
  int ttlSeconds = -1
  final Map<String, Object> attributes = [:]
  final Map<String, Collection<String>> relationships = [:].withDefault { [] as Set }

  public MutableCacheData(String id) {
    this.id = id
  }

  @JsonCreator
  public MutableCacheData(@JsonProperty("id") String id,
                          @JsonProperty("attributes") Map<String, Object> attributes,
                          @JsonProperty("relationships") Map<String, Collection<String>> relationships) {
    this(id);
    this.attributes.putAll(attributes);
    this.relationships.putAll(relationships);
  }

  public static Map<String, MutableCacheData> mutableCacheMap() {
    return [:].withDefault { String id -> new MutableCacheData(id) }
  }
}

