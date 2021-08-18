CREATE SCHEMA IF NOT EXISTS vrt;

CREATE TABLE vrt.party_shop_reference(
  shop_id                                         CHARACTER VARYING           NOT NULL,
  party_id                                        CHARACTER VARYING           NOT NULL,
  category_type                                   CHARACTER VARYING           NOT NULL,
  CONSTRAINT party_shop_reference_pkey PRIMARY KEY (shop_id, party_id)
);

CREATE INDEX party_category_shop_reference_id on vrt.party_shop_reference(party_id, category_type);
