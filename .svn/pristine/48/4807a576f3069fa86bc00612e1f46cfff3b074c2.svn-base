-- Drop old rules
DROP RULE IF EXISTS offering_insert_actualization ON observation;
DROP RULE IF EXISTS offering_update_actualization ON observation;
DROP RULE IF EXISTS offering_delete_actualization ON observation;

-----------------------------------------------------------------------------------------------------------------------
-- trigger for actualization of min_time and max_time for offering, if an insert or a delete on table observation is done
-----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION offering_update()
RETURNS trigger AS 
$BODY$
DECLARE
row_data offering%rowtype;
BEGIN
FOR row_data IN SELECT * FROM offering 
    LOOP
        UPDATE offering SET min_time = (SELECT min(time_stamp) FROM observation WHERE observation.offering_id= row_data.offering_id) WHERE offering.offering_id= row_data.offering_id;
        UPDATE offering SET max_time = (SELECT max(time_stamp) FROM observation WHERE observation.offering_id= row_data.offering_id) WHERE offering.offering_id= row_data.offering_id;
    END LOOP;
return new;
END
$BODY$
 LANGUAGE 'plpgsql' VOLATILE;

CREATE TRIGGER offering_actualization
  AFTER INSERT OR UPDATE OR DELETE
  ON observation
  FOR EACH STATEMENT
  EXECUTE PROCEDURE offering_update();