package com.rbkmoney.vortigon.config

import com.rbkmoney.damsel.domain_config.RepositoryClientSrv
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class AppConfig {

    @Bean
    fun repositoryClient(
        @Value("\${repository.url}") resource: Resource,
        @Value("\${repository.network-timeout}") networkTimeout: Int,
    ): RepositoryClientSrv.Iface {
        return THSpawnClientBuilder()
            .withAddress(resource.uri)
            .withNetworkTimeout(networkTimeout)
            .build(RepositoryClientSrv.Iface::class.java)
    }
}
