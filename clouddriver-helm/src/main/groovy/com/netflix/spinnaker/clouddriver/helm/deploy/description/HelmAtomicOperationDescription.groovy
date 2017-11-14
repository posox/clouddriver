package com.netflix.spinnaker.clouddriver.helm.deploy.description

import com.netflix.spinnaker.clouddriver.deploy.DeployDescription
import com.netflix.spinnaker.clouddriver.helm.security.HelmCredentials
import groovy.transform.AutoClone
import groovy.transform.Canonical

@AutoClone
@Canonical
class HelmAtomicOperationDescription implements DeployDescription {
  HelmCredentials helmCredentials
  String credentials
}
