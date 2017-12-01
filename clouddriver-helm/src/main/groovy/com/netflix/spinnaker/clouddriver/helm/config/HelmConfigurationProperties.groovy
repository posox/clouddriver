package com.netflix.spinnaker.clouddriver.helm.config

import groovy.transform.ToString

@ToString(includeNames = true)
class HelmConfigurationProperties {

  @ToString(includeNames = true)
  static class ManagedAccount {

    @ToString(includeNames = true)
    static class HelmRepo {
      String name
      String address
    }

    String name
    String environment
    String accountType
    String kubeconfigFile
    String tillerNamespace
    List<HelmRepo> helmRepos = []
  }

  List<ManagedAccount> accounts = []
}
