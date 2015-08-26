--function for getting timestamp as iso
CREATE OR REPLACE FUNCTION
 iso_timestamp(timestamp with time zone)
   RETURNS varchar as $$
  SELECT replace(replace(replace(text(xmlelement(name x, $1)),'<x>',''),'</x>',''),'<x/>','')::varchar
$$ language sql immutable;