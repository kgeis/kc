create table AWARD_SYNC_CHANGE (
  AWARD_SYNC_CHANGE_ID   DECIMAL(22,0),
  AWARD_ID            DECIMAL(22,0) NOT NULL,
  XML_CHANGES         CLOB,
  CLASS_NAME          VARCHAR(100) NOT NULL,
  ATTRIBUTE_NAME      VARCHAR(50),
  OBJECT_DESC         VARCHAR(500),
  DATA_DESC	          VARCHAR(500),
  SYNC_TYPE           CHAR(1) NOT NULL,
  SYNC_DESCEND    VARCHAR(6),
  SYNC_FABRICATED_DESCEND CHAR(1) NOT NULL,
  SYNC_COST_SHARE_DESCEND CHAR(1) NOT NULL,
  UPDATE_TIMESTAMP    DATE NOT NULL,
  UPDATE_USER         VARCHAR(60) NOT NULL,
  VER_NBR             DECIMAL(8,0) NOT NULL,
  OBJ_ID              VARCHAR(36)
);

ALTER TABLE AWARD_SYNC_CHANGE
	ADD CONSTRAINT PK_AWARD_SYNC_CHANGE 
		PRIMARY KEY (AWARD_SYNC_CHANGE_ID);
	