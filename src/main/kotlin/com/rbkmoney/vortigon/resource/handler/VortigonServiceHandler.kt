package com.rbkmoney.vortigon.resource.handler

import com.rbkmoney.damsel.domain.CategoryType
import com.rbkmoney.damsel.vortigon.PartyFilterRequest
import com.rbkmoney.damsel.vortigon.PaymentInstitutionRealm
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv
import com.rbkmoney.vortigon.entity.PartyShopReference
import com.rbkmoney.vortigon.repository.PartyShopReferenceRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

private val log = KotlinLogging.logger {}

@Component
class VortigonServiceHandler(
    private val partyShopReferenceRepository: PartyShopReferenceRepository,
) : VortigonServiceSrv.Iface {

    override fun getShopsIds(partyId: String, paymentInstitutionRealm: PaymentInstitutionRealm): MutableList<String> {
        log.debug("-> get shops ids by partyId: $partyId env: $paymentInstitutionRealm")
        val references: List<PartyShopReference> =
            partyShopReferenceRepository.findByPartyShopPKPartyIdAndCategoryType(
                partyId,
                resolveCategoryType(paymentInstitutionRealm)
            )
        log.debug("-> get shops ids by partyId: $partyId env: $paymentInstitutionRealm result: $references")
        return if (!CollectionUtils.isEmpty(references)) {
            references.mapNotNull { it.partyShopPK?.shopId }.toMutableList()
        } else mutableListOf()
    }

    override fun findPartyIds(p0: PartyFilterRequest): MutableList<String> {
        TODO("Not yet implemented")
    }

    private fun resolveCategoryType(paymentInstitutionRealm: PaymentInstitutionRealm): String {
        return when (paymentInstitutionRealm) {
            PaymentInstitutionRealm.live -> CategoryType.live.name
            PaymentInstitutionRealm.test -> CategoryType.test.name
            else -> throw IllegalArgumentException("resolveCategoryType environment: $paymentInstitutionRealm is unknown!")
        }
    }
}
