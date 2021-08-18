package com.rbkmoney.vortigon.repository

import com.rbkmoney.vortigon.entity.PartyShopPK
import com.rbkmoney.vortigon.entity.PartyShopReference
import org.springframework.data.jpa.repository.JpaRepository

interface PartyShopReferenceRepository : JpaRepository<PartyShopReference, PartyShopPK> {

    fun findByPartyShopPKPartyIdAndCategoryType(partyId: String, type: String): List<PartyShopReference>

    fun findByPartyShopPK(partyShopPk: PartyShopPK): PartyShopReference?
}
