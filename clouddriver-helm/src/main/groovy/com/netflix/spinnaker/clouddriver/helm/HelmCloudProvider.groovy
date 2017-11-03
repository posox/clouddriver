package com.netflix.spinnaker.clouddriver.helm

import com.netflix.spinnaker.clouddriver.core.CloudProvider
import org.springframework.stereotype.Component

import java.lang.annotation.Annotation

@Component
class HelmCloudProvider implements CloudProvider {
  static final String ID = "helm"
  final String id = ID
  final String displayName = "Helm"
  final Class<Annotation> operationAnnotationType = HelmOperation

}
