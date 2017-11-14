package com.netflix.spinnaker.clouddriver.helm.model

import com.fasterxml.jackson.core.type.TypeReference
import com.netflix.spinnaker.clouddriver.model.Application
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

@CompileStatic
@EqualsAndHashCode(includes = ["name"])
class HelmApplication implements Application, Serializable {
  public static final TypeReference<Map<String, String>> ATTRIBUTES = new TypeReference<Map<String, String>>() {}
  final String name
  final Map<String, String> attributes
  final Map<String, Set<String>> clusterNames

  HelmApplication(String name, Map<String, String> attributes, Map<String, Set<String>> clusterNames) {
    this.name = name
    this.attributes = attributes
    this.clusterNames = clusterNames
  }
}
