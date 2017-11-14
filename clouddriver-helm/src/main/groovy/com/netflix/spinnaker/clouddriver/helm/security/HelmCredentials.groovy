package com.netflix.spinnaker.clouddriver.helm.security

import com.netflix.spinnaker.clouddriver.helm.HelmJobExecutor
import com.netflix.spinnaker.clouddriver.helm.client.HelmClient

class HelmCredentials {
  final HelmClient client

  HelmCredentials(HelmJobExecutor jobExecutor, String tillerNamespace, String kubeconfigFile) {
    this.client = new HelmClient(jobExecutor, tillerNamespace, kubeconfigFile)
  }
}
