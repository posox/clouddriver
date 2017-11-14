package com.netflix.spinnaker.clouddriver.helm.deploy

import com.netflix.spinnaker.clouddriver.helm.security.HelmCredentials
import com.netflix.spinnaker.clouddriver.helpers.AbstractServerGroupNameResolver

class HelmServerGroupNameResolver extends AbstractServerGroupNameResolver {

  private static final String PHASE = "DEPLOY"

  private final String namespace
  private final HelmCredentials credentials

  HelmServerGroupNameResolver(String namespace, HelmCredentials credentials) {
    this.namespace = namespace
    this.credentials = credentials
  }

  @Override
  String getPhase() {
    PHASE
  }

  @Override
  String getRegion() {
    namespace
  }

  @Override
  List<AbstractServerGroupNameResolver.TakenSlot> getTakenSlots(String clusterName) {
    return null
  }
}
