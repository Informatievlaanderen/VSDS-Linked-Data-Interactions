CREATE TABLE sensor
(
    sensor_id               varchar(4096),
    description             varchar(4096),
    identifier              varchar(256),
    version_of              varchar(4096),
    wgs84_pos_lat           numeric(20, 16),
    wgs84_pos_long          numeric(20, 16),
    generated_at_time       datetimeoffset,
    has_feature_of_interest varchar(4096),
    lat_Lambert72           numeric(20, 12),
    long_lambert72          numeric(20, 12),
    quality_label           varchar(32),
    modified_at             datetimeoffset,
    valid_from              datetimeoffset,
    valid_to                datetimeoffset,
    serial_number           varchar(4096),
    vendor                  varchar(4096),
    device_id               varchar(4096),
    device_model            varchar(4096),
    device_name             varchar(4096),
    device_status           varchar(256),
    owner                   varchar(256)
);