package com.rbkmoney.vortigon.handler.party.shop

import com.rbkmoney.damsel.domain.Category
import com.rbkmoney.damsel.domain.Shop
import com.rbkmoney.damsel.payment_processing.PartyChange
import com.rbkmoney.machinegun.eventsink.MachineEvent
import com.rbkmoney.vortigon.entity.PartyShopPK
import com.rbkmoney.vortigon.entity.PartyShopReference
import com.rbkmoney.vortigon.extension.getClaimStatus
import com.rbkmoney.vortigon.handler.ChangeHandler
import com.rbkmoney.vortigon.handler.constant.HandleEventType
import com.rbkmoney.vortigon.repository.PartyShopReferenceRepository
import com.rbkmoney.vortigon.service.DomainRepositoryAdapter
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class ShopCreatedPartyShopReferenceHandler(
    private val domainRepositoryAdapter: DomainRepositoryAdapter,
    private val partyShopReferenceRepository: PartyShopReferenceRepository,
) : ChangeHandler<PartyChange, MachineEvent> {

    override fun handleChange(change: PartyChange, event: MachineEvent) {
        val claimCreated = change.claimCreated
        val claimStatus = change.getClaimStatus() ?: throw IllegalStateException("Claim status can't be null")
        val shopEffects = claimStatus.accepted.effects.filter { it.isSetShopEffect }.map { it.shopEffect }
        for (shopEffectUnit in shopEffects) {
            val shopEffect = shopEffectUnit.effect
            if (shopEffect.isSetCreated) {
                val createdShop: Shop = shopEffect.created
                val category = domainRepositoryAdapter.getCategory(createdShop.getCategory())
                val shopReference = PartyShopReference().apply {
                    partyShopPK = PartyShopPK().apply {
                        shopId = shopEffectUnit.shopId
                        partyId = event.sourceId
                    }
                    categoryType = category.getType().name
                }
                val partyShopReference = partyShopReferenceRepository.save(shopReference)
                log.info("Save created partyShopReference: $partyShopReference}")
            } else if (shopEffect.isSetCategoryChanged) {
                val partyShopReference: PartyShopReference? = partyShopReferenceRepository
                    .findByPartyShopPK(
                        PartyShopPK().apply {
                            shopId = shopEffectUnit.shopId
                            partyId = event.sourceId
                        }
                    )
                if (partyShopReference == null) {
                    log.warn("Can't find reference with shopId: ${shopEffectUnit.shopId}")
                    throw IllegalStateException("Can't find reference for shopId: ${shopEffectUnit.shopId}")
                }
                val category: Category = domainRepositoryAdapter.getCategory(shopEffect.categoryChanged)
                partyShopReference.categoryType = category.getType().name
                partyShopReferenceRepository.save(partyShopReference)
                log.info("Save created partyShopReference: $partyShopReference")
            }
        }
    }

    override fun accept(change: PartyChange): Boolean {
        return super.accept(change) &&
            change.getClaimStatus()?.accepted?.effects?.any { it.isSetShopEffect } == true
    }

    override val changeType: HandleEventType = HandleEventType.CLAIM_CREATED_FILTER
}
