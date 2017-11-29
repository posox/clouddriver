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
    def jobStatus = jobExecutor.runCommand(command)

    String stdout = jobStatus.stdOut
    // remove header
    String result = stdout.substring(stdout.indexOf("\n") + 1)
    List<HelmRelease> helmReleases = []
    for (String release : result.split("\n")) {
      def tokens = release.split("\t")
      helmReleases << new HelmRelease(
        name: tokens[0].trim(),
        namespace: tokens[5].trim(),
        status: tokens[3].trim(),
        chart: tokens[4].trim(),
      )
    }
    helmReleases
  }

  def deleteRelease(String release) {
    def command = ["helm", "delete", "--purge", release]
    execute(command)
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
  String chart
}
