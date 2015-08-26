-- SQL script for changing the datamodel
-- ATTENTION: Before executing this file, you have to do changes in the file (signed as MANDATORY)

----------------------------------------------------------------------
--change observation table (add foreign key offering_id, which references on offering table)

--add column offering_id to observation table (DO NOT CHANGE)
ALTER TABLE observation ADD COLUMN offering_id varchar(100);

--update values of column offering_id for already existing observations (MANDATORY)
--add the offerings to the corresponding observation values through using phenomenon_id as condition
UPDATE observation SET offering_id = 'GAUGE_HEIGHT' WHERE phenomenon_id='urn:ogc:def:phenomenon:OGC:1.0.30:waterlevel' OR phenomenon_id='urn:ogc:def:phenomenon:OGC:1.0.30:reference';
UPDATE observation SET offering_id = 'WATER_SPEED' WHERE phenomenon_id='urn:ogc:def:phenomenon:OGC:1.0.30:waterspeed';

--set constraint not null on column offering id (DO NOT CHANGE)
ALTER TABLE observation ALTER COLUMN offering_id SET NOT NULL;

--add foreign key constraint (DO NOT CHANGE)
ALTER TABLE observation ADD FOREIGN KEY (offering_id) REFERENCES offering ON UPDATE CASCADE;

----------------------------------------------------------------------
--change offering table (add columns min_time and max_time)
--min_time for observation values, which belong to the offering (DO NOT CHANGE)
ALTER TABLE offering ADD COLUMN min_time timestamptz;

--max_time for observation values, which belong to the offering (DO NOT CHANGE)
ALTER TABLE offering ADD COLUMN max_time timestamptz;

--update values for already existing offerings (MANDATORY)
UPDATE offering SET min_time = '2005-10-05 10:15:00+02' WHERE offering_id='GAUGE_HEIGHT';
UPDATE offering SET max_time = '2005-10-10 10:15:00+02' WHERE offering_id='GAUGE_HEIGHT';

--create index on offering_id for observation table (DO NOT CHANGE)
CREATE INDEX offObsTable ON observation(offering_id);

--Table: foi_off
-- represents the m:n relationship between features of interest and offerings (DO NOT CHANGE)
CREATE TABLE foi_off
(
  feature_of_interest_id varchar(100) NOT NULL,
  offering_id varchar(100) NOT NULL,
  PRIMARY KEY (feature_of_interest_id,offering_id)
);

--foreign keys for foi_off table (DO NOT CHANGE)
ALTER TABLE foi_off ADD FOREIGN KEY (feature_of_interest_id) REFERENCES feature_of_interest ON UPDATE CASCADE;
ALTER TABLE foi_off ADD FOREIGN KEY (offering_id) REFERENCES offering ON UPDATE CASCADE;

--insert values into foi_off table (DO NOT CHANGE)
INSERT INTO foi_off (SELECT DISTINCT feature_of_interest_id , offering_id FROM feature_of_interest NATURAL INNER JOIN observation);

-----------------------------------------------------------------------------------------------------------------------
--rule for actualization of min_time and max_time for offering, if an insert on table observation is done (DO NOT CHANGE)
-----------------------------------------------------------------------------------------------------------------------
CREATE RULE offering_time_actualization AS ON INSERT TO observation DO
	(UPDATE offering SET min_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp < offering.min_time OR offering.min_time IS NULL);
	UPDATE offering SET max_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp > offering.max_time OR offering.max_time IS NULL);)
	





