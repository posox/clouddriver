package com.netflix.spinnaker.clouddriver.helm.deploy.ops

import com.netflix.spinnaker.clouddriver.data.task.Task
import com.netflix.spinnaker.clouddriver.data.task.TaskRepository
import com.netflix.spinnaker.clouddriver.deploy.DeploymentResult
import com.netflix.spinnaker.clouddriver.helm.deploy.HelmServerGroupNameResolver
import com.netflix.spinnaker.clouddriver.helm.deploy.description.DeployHelmAtomicOperationDescription
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation

class DeployHelmAtomicOperation implements AtomicOperation<DeploymentResult> {
  private static final String PHASE = "DEPLOY"

  DeployHelmAtomicOperationDescription description

  DeployHelmAtomicOperation(DeployHelmAtomicOperationDescription description) {
    this.description = description
  }

  private static Task getTask() {
    TaskRepository.threadLocalTask.get()
  }

  @Override
  DeploymentResult operate(List priorOutputs) {
    def release = deployDescription()
    DeploymentResult deploymentResult = new DeploymentResult()

    task.updateStatus(PHASE, "Initializing creation of application.")
    deploymentResult.serverGroupNames = ["${release.namespace}:${release.name}".toString()]
    deploymentResult.serverGroupNameByRegion[release.namespace] = release.name

    return deploymentResult
  }

  def deployDescription() {
    task.updateStatus(PHASE, "Prepare deploy command for ${description.chart} chart")

    def namespace = description.namespace
    def credentials = description.helmCredentials

    def serverGroupNameResolver = new HelmServerGroupNameResolver(namespace, credentials)
    // TODO: add freeformDetail support
    def clusterName = serverGroupNameResolver.combineAppStackDetail(description.application, description.release, "")

    task.updateStatus(PHASE, "Deploy ${description.chart} chart")
    description.helmCredentials.client.createRelease(description.chart, clusterName, description?.namespace, description?.values)

    description.helmCredentials.client.listReleases().find {
      it.name == clusterName
    }
  }
}
