package com.netflix.spinnaker.clouddriver.helm.deploy.converters

import com.fasterxml.jackson.databind.DeserializationFeature
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport

class HelmAtomicOperationConverterHelper {
  static Object convertDescription(Map input,
                                   AbstractAtomicOperationsCredentialsSupport credentialsSupport,
                                   Class targetDescriptionType) {
    def rawcreds = input.credentials as String
    def credentials = credentialsSupport.getCredentialsObject(rawcreds as String)?.getCredentials()
    def converted = credentialsSupport.objectMapper
      .copy()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .convertValue(input, targetDescriptionType)

    converted.credentials = credentials
    converted.helmCredentials = credentials

    converted
  }
}
