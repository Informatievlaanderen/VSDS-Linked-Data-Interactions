ALTER TABLE event_cleansed
    ADD CONSTRAINT event_cleansed_event_unique UNIQUE (event);

ALTER TABLE sensor
    ADD CONSTRAINT sensor_sensor_id_unique UNIQUE (sensor_id);
