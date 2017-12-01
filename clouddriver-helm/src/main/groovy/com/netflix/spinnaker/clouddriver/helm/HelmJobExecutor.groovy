package com.netflix.spinnaker.clouddriver.helm

import com.netflix.spinnaker.clouddriver.jobs.JobExecutor
import com.netflix.spinnaker.clouddriver.jobs.JobRequest
import com.netflix.spinnaker.clouddriver.jobs.JobStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class HelmJobExecutor {
  @Value('${helm.jobSleepMs:1000}')
  Long sleepMs

  @Autowired
  JobExecutor jobExecutor

  private Map<String, String> systemEnv

  def build(String tillerNamespace, String kubeconfig) {
    systemEnv = new HashMap<String, String>(System.getenv())
    systemEnv.put("TILLER_NAMESPACE", tillerNamespace)
    systemEnv.put('KUBECONFIG', kubeconfig)
  }

  JobStatus runCommand(List<String> command) {
    String jobId = jobExecutor.startJob(new JobRequest(tokenizedCommand: command),
                                        systemEnv,
                                        new ByteArrayInputStream())
    waitForJobCompletion(jobId)
  }

  JobStatus waitForJobCompletion(String jobId) {
    sleep(sleepMs)
    JobStatus jobStatus = jobExecutor.updateJob(jobId)
    while (jobStatus.state == JobStatus.State.RUNNING) {
      sleep(sleepMs)
      jobStatus = jobExecutor.updateJob(jobId)
    }
    if (jobStatus.result == JobStatus.Result.FAILURE && jobStatus.stdOut) {
      throw new IllegalArgumentException("STDOUT:\n${jobStatus.stdOut}\nSTDERR:\n${jobStatus.stdErr}")
    }
    jobStatus
  }
}
