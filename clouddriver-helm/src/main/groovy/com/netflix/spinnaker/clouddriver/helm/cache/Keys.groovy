package com.netflix.spinnaker.clouddriver.helm.cache

import com.netflix.frigga.Names

class Keys {
  static enum Namespace {
    APPLICATIONS,
    SERVER_GROUPS,
    CLUSTERS,

    static String provider = "helm"

    final String ns

    private Namespace() {
      def parts = name().split('_')

      ns = parts.tail().inject(new StringBuilder(parts.head().toLowerCase())) { val, next -> val.append(next.charAt(0)).append(next.substring(1).toLowerCase()) }
    }

    String toString() {
      ns
    }
  }

  static Map<String, String> parse(String key) {
    def parts = key.split(':')

    if (parts.length < 2) {
      return null
    }

    def result = [provider: parts[0], type: parts[1]]

    if (result.provider != Namespace.provider) {
      return null
    }

    switch (result.type) {
      case Namespace.APPLICATIONS.ns:
        result << [
          application: parts[2]
        ]
        break
      case Namespace.CLUSTERS.ns:
        def names = Names.parseName(parts[4])
        result << [
          account: parts[2],
          application: parts[3],
          name: parts[4],
          cluster: parts[4],
          stack: names.stack,
          detail: names.detail
        ]
        break
      case Namespace.SERVER_GROUPS.ns:
        def names = Names.parseName(parts[4])
        result << [
          account: parts[2],
          name: parts[4],
          namespace: parts[3],
          region: parts[3],
          serverGroup: parts[4],
          application: names.app,
          stack: names.stack,
          cluster: names.cluster,
          detail: names.detail,
          sequence: names.sequence?.toString(),
        ]
        break
      default:
        return null
        break
    }

    result
  }

  static String getApplicationKey(String application) {
    "${Namespace.provider}:${Namespace.APPLICATIONS}:${application}"
  }

  static String getServerGroupKey(String account, String namespace, String name) {
    "${Namespace.provider}:${Namespace.SERVER_GROUPS}:${account}:${namespace}:${name}"
  }

  static String getClusterKey(String account, String application, String clusterName) {
    "${Namespace.provider}:${Namespace.CLUSTERS}:${account}:${application}:${clusterName}"
  }
}
