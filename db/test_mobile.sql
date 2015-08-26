--#######################
--## SOSmobile TEST DATA for SpatialObservation    ###
--#######################


-- sample phenomenon
INSERT INTO phenomenon(phenomenon_id, phenomenon_description, unit, valuetype) VALUES ('urn:ogc:def:phenomenon:OGC:1.0.30:temperature', 'surface temperature', 'Cel','numericType');

--sample offering
INSERT INTO offering VALUES ('TEMPERATURE','The temperature in a certain area',null,null);

-- sample domain feature
INSERT INTO domain_feature(domain_feature_id, domain_feature_name, domain_feature_description, geom, feature_type) VALUES ('investigationArea1', 'City of paderborn', 'Paderborn', GeometryFromText('POLYGON((8.76667 51.7167,8.76667 52.7167,9.76667 52.7167,9.76667 51.7167,8.76667 51.7167))', 4326),'sos:GenericDomainFeature');

-- sample procedure
INSERT INTO procedure(procedure_id, description_url, description_type, sml_file, actual_position, active, mobile) VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3', null, null,
			'<sml:SensorML xsi:schemaLocation="http://www.opengis.net/sensorML/1.0.1 http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd" version="1.0.1" xmlns="http://www.opengis.net/sensorML/1.0.1" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:gml="http://www.opengis.net/gml">
	<sml:identification>
		<sml:IdentifierList>
			<sml:identifier name="URN">
				<sml:Term definition="urn:ogc:def:identifierType:OGC:uniqueID">
					<sml:value>urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="longName">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="shortName">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="modelNumber">
				<sml:Term>
					<sml:value>1234</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="manufacturer">
				<sml:Term>
					<sml:value>stephan</sml:value>
				</sml:Term>
			</sml:identifier>  
		</sml:IdentifierList>
	</sml:identification>
	<sml:classification>
		<sml:ClassifierList>
			<sml:classifier name="intendedApplication">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
			<sml:classifier name="sensorType">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
			<sml:classifier name="phenomenon">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
		</sml:ClassifierList>
	</sml:classification>
	<sml:member>
		<sml:System gml:id="ifgi-sensor-3">        
			<sml:position name="actualPosition">
				<swe:Position fixed="false" referenceFrame="urn:ogc:crs:epsg:4326">
					<gml:srsName></gml:srsName>
					<swe:location>
						<swe:Vector>
							<swe:coordinate name="latitude">
								<swe:Quantity>
									<swe:value>51.883906</swe:value>
								</swe:Quantity>
							</swe:coordinate>
							<swe:coordinate name="longitude">
								<swe:Quantity>
								<swe:value>8.76667</swe:value>
								</swe:Quantity>
							</swe:coordinate>
						</swe:Vector>
					</swe:location>
				</swe:Position>
			</sml:position>
			<sml:inputs>
				<sml:InputList>
					<sml:input name="InputPhenomena">
						<swe:ObservableProperty definition="urn:ogc:def:phenomenon:OGC:1.0.30:temperature"/>
					</sml:input>
				</sml:InputList>
			</sml:inputs>
			<sml:outputs>
				<sml:OutputList>
					<sml:output name="OutputPhenomena">
						<swe:Quantity definition="urn:ogc:def:phenomenon:OGC:1.0.30:temperature">
						<swe:uom code="Cel"/>
						</swe:Quantity>
					</sml:output>
				</sml:OutputList>
			</sml:outputs>
		</sml:System>
	</sml:member>
</sml:SensorML>',GeometryFromText('POINT(8.76667 51.883906)', 4326),true,true);

INSERT INTO procedure(procedure_id, description_url, description_type, sml_file, actual_position, active, mobile) VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4', null, null, 
	'<sml:SensorML xsi:schemaLocation="http://www.opengis.net/sensorML/1.0.1 http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd" version="1.0.1" xmlns="http://www.opengis.net/sensorML/1.0.1" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:gml="http://www.opengis.net/gml">
	<sml:identification>
		<sml:IdentifierList>
			<sml:identifier name="URN">
				<sml:Term definition="urn:ogc:def:identifierType:OGC:uniqueID">
					<sml:value>urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="longName">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="shortName">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="modelNumber">
				<sml:Term>
					<sml:value>1234</sml:value>
				</sml:Term>
			</sml:identifier>
			<sml:identifier name="manufacturer">
				<sml:Term>
					<sml:value>stephan</sml:value>
				</sml:Term>
			</sml:identifier>  
		</sml:IdentifierList>
	</sml:identification>
	<sml:classification>
		<sml:ClassifierList>
			<sml:classifier name="intendedApplication">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
			<sml:classifier name="sensorType">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
			<sml:classifier name="phenomenon">
				<sml:Term>
					<sml:value>temperature</sml:value>
				</sml:Term>
			</sml:classifier>
		</sml:ClassifierList>
	</sml:classification>
	<sml:member>
		<sml:System gml:id="ifgi-sensor-4">        
			<sml:position name="actualPosition">
				<swe:Position fixed="false" referenceFrame="urn:ogc:crs:epsg:4326">
					<gml:srsName></gml:srsName>
					<swe:location>
						<swe:Vector>
							<swe:coordinate name="latitude">
								<swe:Quantity>
									<swe:value>51.883906</swe:value>
								</swe:Quantity>
							</swe:coordinate>
							<swe:coordinate name="longitude">
								<swe:Quantity>
								<swe:value>9.16667</swe:value>
								</swe:Quantity>
							</swe:coordinate>
						</swe:Vector>
					</swe:location>
				</swe:Position>
			</sml:position>
			<sml:inputs>
				<sml:InputList>
					<sml:input name="InputPhenomena">
						<swe:ObservableProperty definition="urn:ogc:def:phenomenon:OGC:1.0.30:temperature"/>
					</sml:input>
				</sml:InputList>
			</sml:inputs>
			<sml:outputs>
				<sml:OutputList>
					<sml:output name="OutputPhenomena">
						<swe:Quantity definition="urn:ogc:def:phenomenon:OGC:1.0.30:temperature">
						<swe:uom code="Cel"/>
						</swe:Quantity>
					</sml:output>
				</sml:OutputList>
			</sml:outputs>
		</sml:System>
	</sml:member>
</sml:SensorML>',GeometryFromText('POINT(9.16667 51.883906)', 4326),true,true);

--procedure history for sensor 1
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','2008-04-01 17:44:00',GeometryFromText('POINT(8.86667 51.883906)',4326),true,true);
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','2008-04-01 17:45:00',GeometryFromText('POINT(8.86667 51.883906)',4326),true,true);
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','2008-04-01 17:46:00',GeometryFromText('POINT(8.96667 51.883906)',4326),true,true);
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','2008-04-01 17:47:00',GeometryFromText('POINT(9.06667 51.883906)',4326),true,true);
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','2008-04-01 17:48:00',GeometryFromText('POINT(9.16667 51.883906)',4326),true,true);

--procedure history for sensor 2
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','2008-04-01 17:44:00',GeometryFromText('POINT(8.86667 51.883906)',4326),true,true );
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','2008-04-01 17:45:00',GeometryFromText('POINT(8.86667 51.883906)',4326),true,true );
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','2008-04-01 17:46:00',GeometryFromText('POINT(8.96667 51.883906)',4326),true,true );
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','2008-04-01 17:47:00',GeometryFromText('POINT(9.06667 51.883906)',4326),true,true );
INSERT INTO procedure_history VALUES('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','2008-04-01 17:48:00',GeometryFromText('POINT(9.16667 51.883906)',4326),true,true );


-------------------------- sample relationships between phenomena, procedures and features of interest
--sample phen_off relationship
INSERT INTO phen_off VALUES ('urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE');

-- sample proc_phen relationship
INSERT INTO proc_phen VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature');
INSERT INTO proc_phen VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature');

INSERT INTO proc_off VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','TEMPERATURE');
INSERT INTO proc_off VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','TEMPERATURE');

INSERT INTO df_off VALUES ('investigationArea1','TEMPERATURE');

INSERT INTO proc_df VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','investigationArea1');
INSERT INTO proc_df VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','investigationArea1');

-- observation at first sampling feature
INSERT INTO feature_of_interest(feature_of_interest_id, feature_of_interest_name, feature_of_interest_description, geom, feature_type) VALUES ('foi_10001', 'FOI_EINS', 'Feature of Interest 1', GeometryFromText('POINT(8.76667 51.883906)', 4326),'sa:SamplingPoint');
INSERT INTO foi_df VALUES ('foi_10001','investigationArea1');
INSERT INTO foi_off VALUES ('foi_10001','TEMPERATURE');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','foi_10001');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','foi_10001');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:44:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_10001','27.0');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:48:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_10001','27.4');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');


INSERT INTO feature_of_interest(feature_of_interest_id, feature_of_interest_name, feature_of_interest_description, geom, feature_type) VALUES ('foi_20001', 'FOI_ZWEI', 'Feature of Interest 2', GeometryFromText('POINT(8.86667 51.883906)', 4326),'sa:SamplingPoint');
INSERT INTO foi_df VALUES ('foi_20001','investigationArea1');
INSERT INTO foi_off VALUES ('foi_20001','TEMPERATURE');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','foi_20001');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','foi_20001');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:45:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_20001','27.1');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

INSERT INTO observation (time_stamp, procedure_id,phenomenon_id,offering_id,feature_of_interest_id,numeric_value) values ('2008-04-01 17:47:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_20001','27.3');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');


INSERT INTO feature_of_interest(feature_of_interest_id, feature_of_interest_name, feature_of_interest_description, geom, feature_type) VALUES ('foi_30001', 'FOI_DREI', 'Feature of Interest 3', GeometryFromText('POINT(8.96667 51.883906)', 4326),'sa:SamplingPoint');
INSERT INTO foi_df VALUES ('foi_30001','investigationArea1');
INSERT INTO foi_off VALUES ('foi_30001','TEMPERATURE');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','foi_30001');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','foi_30001');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:46:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_30001','27.2');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:46:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_30001','27.2');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');


INSERT INTO feature_of_interest(feature_of_interest_id, feature_of_interest_name, feature_of_interest_description, geom, feature_type) VALUES ('foi_40001', 'FOI_VIER', 'Feature of Interest 4', GeometryFromText('POINT(9.06667 51.883906)', 4326),'sa:SamplingPoint');
INSERT INTO foi_df VALUES ('foi_40001','investigationArea1');
INSERT INTO foi_off VALUES ('foi_40001','TEMPERATURE');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','foi_40001');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','foi_40001');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:47:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_40001','27.3');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:45:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_40001','27.1');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');


INSERT INTO feature_of_interest(feature_of_interest_id, feature_of_interest_name, feature_of_interest_description, geom, feature_type) VALUES ('foi_50001', 'FOI_FUENF', 'Feature of Interest 5', GeometryFromText('POINT(9.16667 51.883906)', 4326),'sa:SamplingPoint');
INSERT INTO foi_df VALUES ('foi_50001','investigationArea1');
INSERT INTO foi_off VALUES ('foi_50001','TEMPERATURE');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','foi_50001');
INSERT INTO proc_foi VALUES ('urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','foi_50001');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:48:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-3','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_50001','27.4');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

INSERT INTO observation (time_stamp, procedure_id, phenomenon_id, offering_id, feature_of_interest_id, numeric_value) values ('2008-04-01 17:44:00', 'urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-4','urn:ogc:def:phenomenon:OGC:1.0.30:temperature','TEMPERATURE','foi_50001','27.0');
INSERT INTO obs_df VALUES(currval(pg_get_serial_sequence('observation','observation_id')),'investigationArea1');

