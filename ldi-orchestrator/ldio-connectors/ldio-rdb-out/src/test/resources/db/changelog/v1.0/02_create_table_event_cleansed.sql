CREATE TABLE event_cleansed
(
    event                   varchar(4096),
    version_of              varchar(4096),
    generated               datetimeoffset,
    end_time                datetimeoffset,
    start_time              datetimeoffset,
    has_feature_of_interest varchar(4096),
    made_by_sensor          varchar(4096),
    modified_at             datetimeoffset,
    status                  varchar(256),
    end_timestamp_known     bit
);