DROP TABLE AWARD_AMT_FNA_DISTRIBUTION;

CREATE TABLE AWARD_AMT_FNA_DISTRIBUTION ( 
	AWARD_AMT_FNA_DISTRIBUTION_ID		NUMBER(8,0),
    AWARD_ID       						NUMBER,
    AWARD_NUMBER						VARCHAR2(10),
    SEQUENCE_NUMBER        				NUMBER(8,0),
    AMOUNT_SEQUENCE_NUMBER				NUMBER(4,0),
    BUDGET_PERIOD            			NUMBER(3,0),
    START_DATE							DATE,
    END_DATE 							DATE,
    DIRECT_COST         				NUMBER(12,2),
    INDIRECT_COST		    			NUMBER(12,2),
    UPDATE_TIMESTAMP       				DATE,
    UPDATE_USER            				VARCHAR2(60),
    VER_NBR 							NUMBER(8,0) DEFAULT 1,
	OBJ_ID 								VARCHAR2(36) DEFAULT SYS_GUID()
);

alter table AWARD_AMT_FNA_DISTRIBUTION ADD CONSTRAINT PK_AWARD_AMT_FNA_DISTRIBUTION PRIMARY KEY(AWARD_AMT_FNA_DISTRIBUTION_ID) ENABLE;
alter table AWARD_AMT_FNA_DISTRIBUTION ADD CONSTRAINT U_AWARD_AMT_FNA_DISTRIBUTION UNIQUE(AWARD_AMT_FNA_DISTRIBUTION_ID,AWARD_NUMBER,SEQUENCE_NUMBER,BUDGET_PERIOD) ENABLE;

alter table AWARD_AMT_FNA_DISTRIBUTION add(AWARD_AMOUNT_INFO_ID number(8,0));

alter table AWARD_AMT_FNA_DISTRIBUTION ADD CONSTRAINT FK_AWARD_AMT_FNA_DISTRIBUTION FOREIGN KEY (AWARD_AMOUNT_INFO_ID) REFERENCES AWARD_AMOUNT_INFO(AWARD_AMOUNT_INFO_ID) ENABLE;

commit;