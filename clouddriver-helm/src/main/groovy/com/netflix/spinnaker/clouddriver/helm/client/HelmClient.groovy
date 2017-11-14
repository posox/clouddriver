package com.netflix.spinnaker.clouddriver.helm.client

import com.netflix.spinnaker.clouddriver.helm.HelmJobExecutor
import com.netflix.spinnaker.clouddriver.jobs.JobStatus

class HelmClient {
  final HelmJobExecutor jobExecutor
  final String tillerNamespace
  final String kubeconfigFile

  HelmClient(HelmJobExecutor jobExecutor, String tillerNamespace, String kubeconfigFile) {
    this.jobExecutor = jobExecutor
    this.tillerNamespace = tillerNamespace
    this.kubeconfigFile = kubeconfigFile
  }

  List<HelmRelease> listReleases() {
    def command = ["helm", "list"]
    def status = jobExecutor.runCommand(command)

    String stdout = status.stdOut
    // remove header
    String result = stdout.substring(stdout.indexOf("\n") + 1)
    def helmReleases = []
    for (String release : result.split("\n")) {
      def tokens = release.split("\t")
      def trimTokens = tokens.each { it.trim() }
      helmReleases << new HelmRelease(trimTokens[0].trim(), trimTokens[5].trim(), trimTokens[3].trim())
    }
    helmReleases
  }

  private JobStatus execute(List<String> command) {
    System.setProperty("TILLER_NAMESPACE", this.tillerNamespace)
    System.setProperty("KUBECONFIG", this.kubeconfigFile)
    jobExecutor.runCommand(command)
  }
}

class HelmRelease {
  String name
  String namespace
  String status

  HelmRelease(String name, String namespace, String status) {
    this.name = name
    this.namespace = namespace
    this.status = status
  }
}
