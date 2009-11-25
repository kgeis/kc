CREATE TABLE PROTOCOL_EXEMPT_NUMBER (
    PROTOCOL_EXEMPT_NUMBER_ID NUMBER (12, 0) NOT NULL, 
    PROTOCOL_SPECIAL_REVIEW_ID NUMBER (12, 0) NOT NULL, 
    EXEMPTION_TYPE_CODE VARCHAR2 (3 BYTE) NOT NULL ENABLE, 
    UPDATE_USER VARCHAR2 (60 BYTE) NOT NULL ENABLE, 
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE, 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL ENABLE, 
    OBJ_ID VARCHAR2 (36 BYTE) DEFAULT SYS_GUID () NOT NULL ENABLE) ;

ALTER TABLE PROTOCOL_EXEMPT_NUMBER 
    ADD CONSTRAINT PK_PROTOCOL_EXEMPT_NUMBER 
            PRIMARY KEY (PROTOCOL_EXEMPT_NUMBER_ID) ;