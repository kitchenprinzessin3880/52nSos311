-- Table: domain_feature
-- represents the domain features of an observation
CREATE TABLE domain_feature
(
  domain_feature_id varchar(100) NOT NULL,
  domain_feature_name varchar(100) NOT NULL,
  domain_feature_description varchar(200),
  geom geometry NOT NULL,
  feature_type text NOT NULL,
  schema_link text,
  PRIMARY KEY (domain_feature_id)
);

-- Table: procedure_history
-- represents the procedure history
CREATE TABLE procedure_history
(
  procedure_id varchar(100) NOT NULL,
  time_stamp timestamptz NOT NULL,
  position geometry,
  active boolean,
  mobile boolean,
  PRIMARY KEY (procedure_id,time_stamp)
);

--Table: df_off
-- represents the m:n relationship between domain features and offerings
CREATE TABLE df_off
(
  domain_feature_id varchar(100) NOT NULL,
  offering_id varchar(100) NOT NULL,
  PRIMARY KEY (domain_feature_id,offering_id)
);

--Table: proc_df
-- represents the m:n relationship between domain features and procedures
CREATE TABLE proc_df
(
  procedure_id varchar(100) NOT NULL,
  domain_feature_id varchar(100) NOT NULL,
  PRIMARY KEY (procedure_id,domain_feature_id)
);

-- Table: obs_df
-- represents the m:n relationship between observations and domain features
CREATE TABLE obs_df
(
  observation_id integer NOT NULL,
  domain_feature_id varchar(100) NOT NULL,
  PRIMARY KEY (observation_id,domain_feature_id)
);

-- Table: foi_df
-- represents the m:n relationship between features of interest and domain features
CREATE TABLE foi_df
(
  feature_of_interest_id varchar(100) NOT NULL,
  domain_feature_id varchar(100) NOT NULL,
  PRIMARY KEY (feature_of_interest_id,domain_feature_id)
);

-----------------------------------------------------------------------------------------------------------------------
-- add references and foreign keys
-----------------------------------------------------------------------------------------------------------------------

--foreign keys for df_off table
ALTER TABLE df_off ADD FOREIGN KEY (domain_feature_id) REFERENCES domain_feature ON UPDATE CASCADE;
ALTER TABLE df_off ADD FOREIGN KEY (offering_id) REFERENCES offering ON UPDATE CASCADE;

--foreign keys for proc_df table
ALTER TABLE proc_df ADD FOREIGN KEY (domain_feature_id) REFERENCES domain_feature ON UPDATE CASCADE;
ALTER TABLE proc_df ADD FOREIGN KEY (procedure_id) REFERENCES procedure ON UPDATE CASCADE;

--foreign keys for procedure_history table
ALTER TABLE procedure_history ADD FOREIGN KEY (procedure_id) REFERENCES procedure ON UPDATE CASCADE;

--foreign keys for foi_df table
ALTER TABLE foi_df ADD FOREIGN KEY (feature_of_interest_id) REFERENCES feature_of_interest ON UPDATE CASCADE;
ALTER TABLE foi_df ADD FOREIGN KEY (domain_feature_id) REFERENCES domain_feature ON UPDATE CASCADE;

--foreign keys for obs_df table
ALTER TABLE obs_df ADD FOREIGN KEY (observation_id) REFERENCES observation ON UPDATE CASCADE;
ALTER TABLE obs_df ADD FOREIGN KEY (domain_feature_id) REFERENCES domain_feature ON UPDATE CASCADE;
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

-- Change check for phenomenon table
ALTER TABLE phenomenon DROP CONSTRAINT phenomenon_valuetype_check;
ALTER TABLE phenomenon ADD CHECK (valuetype IN ('textType','numericType', 'spatialType', 'commonType','externalReferenceType'));

-- Change schema_link column in feature_of_interest table
ALTER TABLE feature_of_interest ALTER COLUMN schema_link DROP NOT NULL;

-- Add spatial_value column in observation table
ALTER TABLE observation ADD COLUMN spatial_value geometry; 
   
-- add columns in procedure table
ALTER TABLE procedure ADD COLUMN sml_file text;
ALTER TABLE procedure ADD COLUMN actual_position geometry;
ALTER TABLE procedure ADD COLUMN active boolean;
ALTER TABLE procedure ADD COLUMN mobile boolean;