CREATE TABLE PROTOCOL_EXEMPT_CHKLST (
    PROTOCOL_EXEMPT_CHKLST_ID NUMBER (12, 0) NOT NULL, 
    PROTOCOL_ID NUMBER (12, 0) NOT NULL, 
    SUBMISSION_ID_FK NUMBER (12, 0) NOT NULL, 
    PROTOCOL_NUMBER VARCHAR2 (20) NOT NULL, 
    SEQUENCE_NUMBER NUMBER (4, 0) NOT NULL, 
    SUBMISSION_NUMBER NUMBER (4, 0) NOT NULL, 
    EXEMPT_STUDIES_CHECKLIST_CODE VARCHAR2 (3) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2 (60) NOT NULL, 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2 (36) DEFAULT SYS_GUID () NOT NULL) ;

ALTER TABLE PROTOCOL_EXEMPT_CHKLST 
    ADD CONSTRAINT PK_PROTOCOL_EXEMPT_CHKLST 
            PRIMARY KEY (PROTOCOL_EXEMPT_CHKLST_ID) ;