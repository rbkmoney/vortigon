package com.rbkmoney.vortigon

import com.rbkmoney.damsel.domain.Category
import com.rbkmoney.damsel.domain.CategoryType
import com.rbkmoney.vortigon.entity.PartyShopReference
import com.rbkmoney.vortigon.repository.PartyShopReferenceRepository
import com.rbkmoney.vortigon.service.DomainRepositoryAdapter
import mu.KotlinLogging
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

private val log = KotlinLogging.logger {}

// Test for real big data set
// Need get truststore pcsng-kafka by devops
@Ignore
@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [VortigonApplication::class],
    properties = [
        "kafka.bootstrap-servers=dev-kafka-mirror.bst1.rbkmoney.net:9092",
        "kafka.ssl.trustStoreLocation=src/test/resources/broker/pcsng-kafka.p12",
        "kafka.ssl.trustStorePassword=xxx",
        "kafka.ssl.keyStoreLocation=src/test/resources/broker/strug.p12",
        "kafka.ssl.keyStorePassword=xxx",
        "kafka.ssl.keyPassword=xxx",
        "kafka.ssl.enabled=true",
        "kafka.ssl.keyStoreType=PKCS12",
        "kafka.ssl.trustStoreType=PKCS12",
        "kafka.consumer.group-id=fraud-connector"
    ]
)
class IntegrationTest : PostgresAbstractTest() {

    @Autowired
    lateinit var partyShopReferenceRepository: PartyShopReferenceRepository

    @MockBean
    lateinit var domainRepositoryAdapter: DomainRepositoryAdapter

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        `when`(domainRepositoryAdapter.getCategory(any())).thenReturn(
            Category()
                .setName("test")
                .setType(CategoryType.live)
        )
        Thread.sleep(10000L)
        val all: List<PartyShopReference> = partyShopReferenceRepository.findAll()
        log.info("all: {}", all)
    }
}
