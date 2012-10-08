alter table BUDGET_SUB_AWARD_ATT add SUB_AWARD_ATTACHMENT_ID NUMBER(12, 0);
alter table BUDGET_SUB_AWARD_ATT drop primary key;
alter table BUDGET_SUB_AWARD_ATT add CONSTRAINT PK_BGT_SUB_AWARD_ATT_KRA primary key (SUB_AWARD_ATTACHMENT_ID);

--alter table BUDGET_SUB_AWARD_ATT add constraint UK_BUDGET_SUB_AWARD_ATT unique(BUDGET_ID,SUB_AWARD_NUMBER,CONTENT_ID); 

CREATE SEQUENCE  SEQ_SUB_AWD_BGT_ATT_ID  MINVALUE 1 MAXVALUE 999999 INCREMENT BY 1 START WITH 1 NOCYCLE ;