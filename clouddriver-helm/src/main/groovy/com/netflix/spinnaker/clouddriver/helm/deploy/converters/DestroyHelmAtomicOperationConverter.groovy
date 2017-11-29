package com.netflix.spinnaker.clouddriver.helm.deploy.converters

import com.netflix.spinnaker.clouddriver.helm.HelmOperation
import com.netflix.spinnaker.clouddriver.helm.deploy.description.DeployHelmAtomicOperationDescription
import com.netflix.spinnaker.clouddriver.helm.deploy.ops.DestroyHelmAtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport
import org.springframework.stereotype.Component

@HelmOperation(AtomicOperations.DESTROY_SERVER_GROUP)
@Component
class DestroyHelmAtomicOperationConverter extends AbstractAtomicOperationsCredentialsSupport {
  @Override
  AtomicOperation convertOperation(Map input) {
    new DestroyHelmAtomicOperation(convertDescription(input))
  }

  @Override
  DeployHelmAtomicOperationDescription convertDescription(Map input) {
    HelmAtomicOperationConverterHelper.convertDescription(input, this, DeployHelmAtomicOperationDescription)
  }
}
