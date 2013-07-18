DELIMITER /

ALTER TABLE IACUC_PROTOCOL_PERSONS
    DROP CONSTRAINT FK_IACUC_PROTOCOL_PERS_AFF
/

ALTER TABLE IACUC_PROTOCOL_PERSONS
    ADD CONSTRAINT FK_IACUC_PROTOCOL_PERS_AFF FOREIGN KEY (AFFILIATION_TYPE_CODE)
    REFERENCES IACUC_AFFILIATION_TYPE (AFFILIATION_TYPE_CODE)
/

DELIMITER ;
