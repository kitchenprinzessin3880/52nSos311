--function for getting timestamp as iso
CREATE OR REPLACE FUNCTION
	iso_timestamp(timestamp with time zone) 
		RETURNS varchar as $$ 
	SELECT replace($1,' ','T')||':00'::varchar 
$$ language sql immutable;