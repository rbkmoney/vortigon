package com.rbkmoney.vortigon.entity

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "party_shop_reference")
class PartyShopReference : Serializable {
    @EmbeddedId
    var partyShopPK: PartyShopPK? = null
    var categoryType: String? = null
}
