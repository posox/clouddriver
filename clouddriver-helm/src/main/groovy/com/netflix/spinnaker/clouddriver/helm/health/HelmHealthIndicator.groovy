package com.netflix.spinnaker.clouddriver.helm.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class HelmHealthIndicator implements HealthIndicator {
  @Override
  Health health() {
    // TODO: implement health indicator
    new Health.Builder().up().build()
  }
}
