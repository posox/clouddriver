package com.netflix.spinnaker.clouddriver.helm.config

import com.netflix.spinnaker.clouddriver.helm.HelmJobExecutor
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

    void installTiller(HelmJobExecutor jobExecutor) {
      if (this.kubeconfigFile) {
        // TODO: fix it for multiaccount suport
        System.setProperty("KUBECONFIG", this.kubeconfigFile)
        System.setProperty("TILLER_NAMESPACE", this.tillerNamespace)
      }
      List<String> command = ["helm", "init"]
      jobExecutor.runCommand(command)
    }
  }

  List<ManagedAccount> accounts = []
}
