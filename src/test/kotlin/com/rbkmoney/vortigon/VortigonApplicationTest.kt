package com.rbkmoney.vortigon

import com.rbkmoney.damsel.domain.Active
import com.rbkmoney.damsel.domain.Blocking
import com.rbkmoney.damsel.domain.Category
import com.rbkmoney.damsel.domain.CategoryRef
import com.rbkmoney.damsel.domain.CategoryType
import com.rbkmoney.damsel.domain.Shop
import com.rbkmoney.damsel.domain.ShopDetails
import com.rbkmoney.damsel.domain.ShopLocation
import com.rbkmoney.damsel.domain.Suspension
import com.rbkmoney.damsel.domain.Unblocked
import com.rbkmoney.damsel.payment_processing.Claim
import com.rbkmoney.damsel.payment_processing.ClaimAccepted
import com.rbkmoney.damsel.payment_processing.ClaimEffect
import com.rbkmoney.damsel.payment_processing.ClaimStatus
import com.rbkmoney.damsel.payment_processing.PartyChange
import com.rbkmoney.damsel.payment_processing.PartyModification
import com.rbkmoney.damsel.payment_processing.ShopEffect
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit
import com.rbkmoney.damsel.payment_processing.ShopModification
import com.rbkmoney.damsel.payment_processing.ShopModificationUnit
import com.rbkmoney.damsel.vortigon.PaymentInstitutionRealm
import com.rbkmoney.machinegun.eventsink.SinkEvent
import com.rbkmoney.vortigon.repository.PartyShopReferenceRepository
import com.rbkmoney.vortigon.resource.handler.VortigonServiceHandler
import com.rbkmoney.vortigon.service.DomainRepositoryAdapter
import junit.framework.Assert.assertEquals
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [VortigonApplication::class])
class VortigonApplicationTest : AbstractKafkaIntegrationTest() {

    @Value("\${kafka.topics.party-shop.id}")
    lateinit var topic: String

    @Autowired
    lateinit var partyShopReferenceRepository: PartyShopReferenceRepository

    @Autowired
    lateinit var vortigonHandler: VortigonServiceHandler

    @MockBean
    lateinit var domainRepositoryAdapter: DomainRepositoryAdapter

    @Test
    fun contextLoads() {
        whenever(domainRepositoryAdapter.getCategory(anyOrNull())).then {
            Category().apply {
                name = "test"
                type = CategoryType.live
            }
        }
        val partyChange: PartyChange = createPartyChange()
        val message = createMachineEvent(partyChange, SOURCE_ID, 1L)
        val producer: Producer<String, SinkEvent> = createProducer()
        val producerRecord: ProducerRecord<String, SinkEvent> =
            ProducerRecord<String, SinkEvent>(topic, message.sourceId, createSinkEvent(message))
        producer.send(producerRecord).get()
        producer.send(producerRecord).get()
        producer.send(producerRecord).get()
        Thread.sleep(2000L)
        val shopsIds: List<String> = vortigonHandler.getShopsIds(SOURCE_ID, PaymentInstitutionRealm.live)
        Assert.assertEquals(1, shopsIds.size.toLong())
        assertEquals(SHOP_ID, shopsIds[0])
    }

    private fun createPartyChange(): PartyChange {
        return PartyChange.claim_created(
            Claim()
                .setCreatedAt(Instant.now().toString())
                .setChangeset(
                    java.util.List.of(
                        PartyModification.shop_modification(
                            ShopModificationUnit()
                                .setId("s")
                                .setModification(
                                    ShopModification.category_modification(
                                        CategoryRef()
                                            .setId(123)
                                    )
                                )
                        )
                    )
                )
                .setStatus(
                    ClaimStatus.accepted(
                        ClaimAccepted().setEffects(
                            java.util.List.of(
                                ClaimEffect.shop_effect(
                                    ShopEffectUnit(
                                        SHOP_ID,
                                        ShopEffect.created(
                                            Shop()
                                                .setBlocking(
                                                    Blocking.unblocked(
                                                        Unblocked()
                                                            .setReason("123")
                                                            .setSince("123")
                                                    )
                                                )
                                                .setSuspension(Suspension.active(Active().setSince("1")))
                                                .setDetails(
                                                    ShopDetails()
                                                        .setName("name")
                                                )
                                                .setLocation(ShopLocation.url("wer"))
                                                .setCategory(CategoryRef().setId(1))
                                                .setContractId("123")
                                                .setCreatedAt(Instant.now().toString())
                                                .setId(SHOP_ID)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        )
    }

    companion object {
        const val SOURCE_ID = "12"
        const val SHOP_ID = "shop_id"
    }
}
