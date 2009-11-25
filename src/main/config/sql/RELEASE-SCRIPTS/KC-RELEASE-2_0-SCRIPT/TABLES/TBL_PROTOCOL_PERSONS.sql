CREATE TABLE PROTOCOL_PERSONS (
    PROTOCOL_PERSON_ID NUMBER (12, 0) NOT NULL, 
    PROTOCOL_ID NUMBER (12, 0) NOT NULL, 
    PROTOCOL_NUMBER VARCHAR2 (20) NOT NULL, 
    SEQUENCE_NUMBER NUMBER (4, 0) NOT NULL, 
    PERSON_ID VARCHAR2 (40) NULL, 
    PERSON_NAME VARCHAR2 (90) NOT NULL, 
    PROTOCOL_PERSON_ROLE_ID VARCHAR2 (12) , 
    ROLODEX_ID NUMBER (12, 0) NULL, 
    AFFILIATION_TYPE_CODE NUMBER (3, 0) , 
    UPDATE_TIMESTAMP DATE, 
    UPDATE_USER VARCHAR2 (60) , 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2 (36) DEFAULT SYS_GUID () NOT NULL) ;

ALTER TABLE PROTOCOL_PERSONS 
    ADD CONSTRAINT PK_PROTOCOL_PERSONS 
            PRIMARY KEY (PROTOCOL_PERSON_ID) ;