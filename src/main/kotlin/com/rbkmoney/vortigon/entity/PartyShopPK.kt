package com.rbkmoney.vortigon.entity

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class PartyShopPK : Serializable {
    var shopId: String? = null
    var partyId: String? = null
}
