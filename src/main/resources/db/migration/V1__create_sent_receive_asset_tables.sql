CREATE TABLE sent_asset_details (
	id varchar NOT NULL,
	contract_agreement_id varchar(1000) NULL,
	data varchar(10000) NULL,
	status varchar(10) NOT NULL DEFAULT false,
    created_date timestamp NOT NULL,
    CONSTRAINT sent_asset_details_pk PRIMARY KEY (id)
);

CREATE TABLE receive_asset_details (
    id varchar NOT NULL,
    reference_id varchar(1000) NULL,
    contract_agreement_id varchar(1000) NULL,
    data varchar(10000) NULL,
    status varchar(10) NOT NULL DEFAULT false,
    created_date timestamp NOT NULL,
    CONSTRAINT receive_asset_details_pk PRIMARY KEY (id)
);


CREATE TABLE provider_submodel_data (
  id VARCHAR(255) NOT NULL,
   data TEXT,
   submodel_identifier VARCHAR(255),
   created_date TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT pk_provider_submodel_data PRIMARY KEY (id)
);