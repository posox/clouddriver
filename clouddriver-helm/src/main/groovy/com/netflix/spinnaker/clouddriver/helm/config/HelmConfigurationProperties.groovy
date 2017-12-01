package com.netflix.spinnaker.clouddriver.helm.config

import groovy.transform.ToString

@ToString(includeNames = true)
class HelmConfigurationProperties {
  @ToString(includeNames = true)
  static class ManagedAccount {
    String name
    String environment
    String accountType
    String kubeconfigFile
    String tillerNamespace
  }

  List<ManagedAccount> accounts = []
}
