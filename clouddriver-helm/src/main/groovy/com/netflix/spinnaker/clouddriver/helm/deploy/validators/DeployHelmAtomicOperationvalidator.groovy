package com.netflix.spinnaker.clouddriver.helm.deploy.validators

import com.netflix.spinnaker.clouddriver.deploy.DescriptionValidator
import com.netflix.spinnaker.clouddriver.helm.HelmOperation
import com.netflix.spinnaker.clouddriver.helm.deploy.description.DeployHelmAtomicOperationDescription
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@HelmOperation(AtomicOperations.CREATE_SERVER_GROUP)
@Component("deployHelmAtomicOperationValidator")
class DeployHelmAtomicOperationvalidator extends DescriptionValidator<DeployHelmAtomicOperationDescription> {
  @Override
  void validate(List priorDescriptions, DeployHelmAtomicOperationDescription description, Errors errors) {
    // TODO: implement
  }
}
