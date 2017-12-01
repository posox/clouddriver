package com.netflix.spinnaker.clouddriver.helm.client

import com.netflix.spinnaker.clouddriver.helm.HelmJobExecutor
import com.netflix.spinnaker.clouddriver.jobs.JobStatus

class HelmClient {
  private final HelmJobExecutor jobExecutor

  HelmClient(HelmJobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor
  }

  def installTiller() {
    def command = ["helm", "init"]
    execute(command)
  }

  List<HelmRelease> listReleases() {
    def command = ["helm", "list"]
    def jobStatus = execute(command)

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

  def createRelease(String chart, String name, String namespace = null) {
    def command = ["helm", "install", chart, "--name", name]
    if (namespace) {
      command << "--namespace" << namespace
    }
    execute(command)
  }

  def deleteRelease(String release) {
    def command = ["helm", "delete", "--purge", release]
    execute(command)
  }

  private JobStatus execute(List<String> command) {
    jobExecutor.runCommand(command)
  }
}

class HelmRelease {
  String name
  String namespace
  String status
  String chart
}
