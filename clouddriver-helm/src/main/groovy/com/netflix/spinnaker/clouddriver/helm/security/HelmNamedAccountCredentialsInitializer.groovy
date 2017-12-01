package com.netflix.spinnaker.clouddriver.helm.security

import com.netflix.spinnaker.cats.module.CatsModule
import com.netflix.spinnaker.cats.provider.ProviderSynchronizerTypeWrapper
import com.netflix.spinnaker.clouddriver.helm.HelmJobExecutor
import com.netflix.spinnaker.clouddriver.helm.config.HelmConfigurationProperties
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsRepository
import com.netflix.spinnaker.clouddriver.security.CredentialsInitializerSynchronizable
import com.netflix.spinnaker.clouddriver.security.ProviderUtils
import org.apache.log4j.Logger
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Configuration
class HelmNamedAccountCredentialsInitializer implements CredentialsInitializerSynchronizable {
  private static final log = Logger.getLogger(this.class.simpleName)

  @Override
  String getCredentialsSynchronizationBeanName() {
    "synchronizeHelmAccounts"
  }

  @Bean
  List<? extends HelmNamedAccountCredentials> helmNamedAccountCredentials(
    String clouddriverUserAgentApplicationName,
    HelmConfigurationProperties helmConfigurationProperties,
    ApplicationContext applicationContext,
    AccountCredentialsRepository accountCredentialsRepository,
    List<ProviderSynchronizerTypeWrapper> providerSynchronizerTypeWrappers,
    HelmJobExecutor jobExecutor
  ) {
    synchronizeHelmAccounts(clouddriverUserAgentApplicationName, helmConfigurationProperties, null, applicationContext, accountCredentialsRepository, providerSynchronizerTypeWrappers, jobExecutor)
  }

  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Bean
  List<? extends HelmNamedAccountCredentials> synchronizeHelmAccounts(
    String clouddriverUserAgentApplicationName,
    HelmConfigurationProperties helmConfigurationProperties,
    CatsModule catsModule,
    ApplicationContext applicationContext,
    AccountCredentialsRepository accountCredentialsRepository,
    List<ProviderSynchronizerTypeWrapper> providerSynchronizerTypeWrappers,
    HelmJobExecutor jobExecutor) {
    def (ArrayList<HelmConfigurationProperties.ManagedAccount> accountsToAdd, List<String> namesOfDeletedAccounts) =
      ProviderUtils.calculateAccountDeltas(accountCredentialsRepository,
                                           HelmNamedAccountCredentials,
                                           helmConfigurationProperties.accounts)

    accountsToAdd.each {HelmConfigurationProperties.ManagedAccount managedAccount ->
      try {
        def helmAccount = new HelmNamedAccountCredentials(managedAccount.name,
                                                          managedAccount.environment ?: managedAccount.name,
                                                          managedAccount.accountType ?: managedAccount.name,
                                                          jobExecutor)
        jobExecutor.build(managedAccount.tillerNamespace ?: "kube-system", managedAccount.kubeconfigFile ?: "/kubeconfig")
        helmAccount.credentials.client.installTiller()
        accountCredentialsRepository.save(managedAccount.name, helmAccount)
      } catch (e) {
        log.info("Couldn't load account ${managedAccount.name} for Helm", e)
      }
    }

    ProviderUtils.unscheduleAndDeregisterAgents(namesOfDeletedAccounts, catsModule)

    if (accountsToAdd && catsModule) {
      ProviderUtils.synchronizeAgentProviders(applicationContext, providerSynchronizerTypeWrappers)
    }

    accountCredentialsRepository.all.findAll {
      it instanceof HelmNamedAccountCredentials
    } as List<HelmNamedAccountCredentials>
  }
}
