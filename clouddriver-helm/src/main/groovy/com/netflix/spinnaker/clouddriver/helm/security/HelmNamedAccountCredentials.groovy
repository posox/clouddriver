package com.netflix.spinnaker.clouddriver.helm.security

import com.netflix.spinnaker.clouddriver.security.AccountCredentials
import groovy.util.logging.Slf4j

@Slf4j
class HelmNamedAccountCredentials implements AccountCredentials<HelmCredentials> {
  final private String cloudProvider = "helm"
  final private String name
  final private String environment
  final private String accountType
  private HelmCredentials credentials
  private final List<String> requiredGroupMembership

  HelmNamedAccountCredentials(String name, String environment, String accountType, List<String> requiredroupMembership) {
    this.name = name
    this.environment = environment
    this.accountType = accountType
    this.requiredGroupMembership = requiredGroupMembership
    this.credentials = buildCredentials()
  }

  private HelmCredentials buildCredentials() {
    new HelmCredentials()
  }

  @Override
  String getName() {
    return name
  }

  @Override
  String getEnvironment() {
    return environment
  }

  @Override
  String getAccountType() {
    return accountType
  }

  @Override
  HelmCredentials getCredentials() {
    return credentials
  }

  @Override
  String getCloudProvider() {
    return cloudProvider
  }

  @Override
  List<String> getRequiredGroupMembership() {
    return requiredGroupMembership
  }
}
