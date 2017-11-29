package com.netflix.spinnaker.clouddriver.helm.deploy.ops

import com.netflix.spinnaker.clouddriver.data.task.Task
import com.netflix.spinnaker.clouddriver.data.task.TaskRepository
import com.netflix.spinnaker.clouddriver.helm.deploy.description.DeployHelmAtomicOperationDescription
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation

class DestroyHelmAtomicOperation implements AtomicOperation<Void> {
  private static final String BASE_PHASE = "DESTROY"

  DeployHelmAtomicOperationDescription description

  DestroyHelmAtomicOperation(DeployHelmAtomicOperationDescription description) {
    this.description = description
  }

  private static Task getTask() {
    TaskRepository.threadLocalTask.get()
  }

  @Override
  Void operate(List priorOutputs) {
    task.updateStatus(BASE_PHASE, "Initializing destroy of server group.")

    def release = description.serverGroupName

    task.updateStatus(BASE_PHASE, "Delete ${release} helm release.")
    description.helmCredentials.client.deleteRelease(release)

    null
  }
}
