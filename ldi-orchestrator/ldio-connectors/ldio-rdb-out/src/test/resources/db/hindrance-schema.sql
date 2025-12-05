CREATE TABLE hindrance (
    gipod_id        bigint,
    adms_identifier varchar(256),
    description    varchar(256),
    zone           varchar(4096),
    modified       datetimeoffset
);