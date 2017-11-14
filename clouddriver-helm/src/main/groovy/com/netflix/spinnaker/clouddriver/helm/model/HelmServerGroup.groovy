package com.netflix.spinnaker.clouddriver.helm.model

import com.netflix.spinnaker.clouddriver.helm.HelmCloudProvider
import com.netflix.spinnaker.clouddriver.model.ServerGroup
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

@CompileStatic
@EqualsAndHashCode(includes = ["name"])
class HelmServerGroup implements ServerGroup, Serializable {
  String name
  final String type = HelmCloudProvider.ID
  final String cloudProvider = HelmCloudProvider.ID
  String account
  String region
  Long createdTime
  Set<String> zones
  Set<Void> instances
  Set<Void> loadBalancers
  Set<Void> securityGroups
  Map<String, Object> launchConfig

  HelmServerGroup() {}

  HelmServerGroup(String name, String namespace, String accountName) {
    this.name = name
    this.region = namespace
    this.account = accountName
  }

  @Override
  Boolean isDisabled() {
    return false
  }

  @Override
  ServerGroup.InstanceCounts getInstanceCounts() {
    new ServerGroup.InstanceCounts(
      down: 0,
      outOfService: 0,
      up: 1,
      starting: 0,
      unknown: 0
    )
  }

  @Override
  ServerGroup.Capacity getCapacity() {
    new ServerGroup.Capacity(min: 0, max: 1, desired: 0)
  }

  @Override
  ServerGroup.ImagesSummary getImagesSummary() {
    return null
  }

  @Override
  ServerGroup.ImageSummary getImageSummary() {
    return null
  }
}
