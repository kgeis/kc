CREATE TABLE AWARD_BUDGET_EXT (
    BUDGET_ID NUMBER (12, 0) NOT NULL ENABLE, 
    DOCUMENT_NUMBER NUMBER (10, 0) ,
    AWARD_BUDGET_STATUS_CODE VARCHAR2 (3) NOT NULL, 
    AWARD_BUDGET_TYPE_CODE VARCHAR2 (3) NOT NULL, 
    OBLIGATED_AMOUNT NUMBER (12,2),
    BUDGET_INITIATOR VARCHAR2(60),
    DESCRIPTION VARCHAR2(255),
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2 (36 BYTE) DEFAULT SYS_GUID () NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2 (60 BYTE) NOT NULL) ;

ALTER TABLE AWARD_BUDGET_EXT 
    ADD CONSTRAINT AWARD_BUDGET_EXT_PK 
            PRIMARY KEY (BUDGET_ID) ;