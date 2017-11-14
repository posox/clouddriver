package com.netflix.spinnaker.clouddriver.helm.deploy.description

import com.netflix.spinnaker.clouddriver.deploy.DeployDescription
import com.netflix.spinnaker.clouddriver.helm.security.HelmNamedAccountCredentials
import groovy.transform.AutoClone
import groovy.transform.Canonical

@AutoClone
@Canonical
class DeployHelmAtomicOperationDescription extends HelmAtomicOperationDescription implements DeployDescription {
  String chart
  String release
  String values
  String application
  String namespace
  String region
  String account
  String serverGroupName

}
