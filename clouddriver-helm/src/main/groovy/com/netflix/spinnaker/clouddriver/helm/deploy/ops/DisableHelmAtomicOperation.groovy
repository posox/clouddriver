package com.netflix.spinnaker.clouddriver.helm.deploy.ops

import com.netflix.spinnaker.clouddriver.helm.deploy.description.HelmAtomicOperationDescription
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation

class DisableHelmAtomicOperation implements AtomicOperation<Void> {
  HelmAtomicOperationDescription description

  DisableHelmAtomicOperation(HelmAtomicOperationDescription description) {
    this.description = description
  }

  @Override
  Void operate(List priorOutputs) {
    return null
  }
}
