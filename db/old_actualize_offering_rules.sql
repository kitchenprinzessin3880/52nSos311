--rule for insert
CREATE RULE offering_insert_actualization AS ON INSERT TO observation DO
	(UPDATE offering SET min_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp < offering.min_time OR offering.min_time IS NULL);
	UPDATE offering SET max_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp > offering.max_time OR offering.max_time IS NULL);
	);

--rule for update
CREATE RULE offering_update_actualization AS ON UPDATE TO observation DO	
	(UPDATE offering SET min_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp < offering.min_time OR offering.min_time IS NULL);
	UPDATE offering SET max_time = NEW.time_stamp WHERE NEW.offering_id=offering.offering_id AND (NEW.time_stamp > offering.max_time OR offering.max_time IS NULL);
	);

--rule for delete
CREATE RULE offering_delete_actualization AS ON DELETE TO observation DO
	(UPDATE offering SET min_time = (SELECT min(time_stamp) FROM observation WHERE OLD.offering_id=offering.offering_id) WHERE OLD.offering_id=offering.offering_id AND (OLD.time_stamp = offering.min_time OR offering.min_time IS NULL);
	UPDATE offering SET max_time = (SELECT max(time_stamp) FROM observation WHERE OLD.offering_id=offering.offering_id) WHERE OLD.offering_id=offering.offering_id AND (OLD.time_stamp = offering.max_time OR offering.max_time IS NULL);
	);