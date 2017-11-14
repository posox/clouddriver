package com.netflix.spinnaker.clouddriver.helm.model

import com.netflix.spinnaker.clouddriver.helm.cache.Keys
import com.netflix.spinnaker.clouddriver.model.Cluster
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

@CompileStatic
@EqualsAndHashCode(includes = ["name", "accountName"])
class HelmCluster implements Cluster, Serializable {
  String name
  String type = Keys.Namespace.provider
  String accountName
  Set<HelmServerGroup> serverGroups = Collections.synchronizedSet(new HashSet<HelmServerGroup>())
  Set<Void> loadBalancers = Collections.synchronizedSet(new HashSet<Void>())
}
