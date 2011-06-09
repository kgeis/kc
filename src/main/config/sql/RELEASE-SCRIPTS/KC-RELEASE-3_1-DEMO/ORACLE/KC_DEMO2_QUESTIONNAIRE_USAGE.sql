INSERT INTO QUESTIONNAIRE_USAGE(QUESTIONNAIRE_USAGE_ID, MODULE_ITEM_CODE, MODULE_SUB_ITEM_CODE, QUESTIONNAIRE_REF_ID_FK, QUESTIONNAIRE_SEQUENCE_NUMBER, RULE_ID, QUESTIONNAIRE_LABEL, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID) 
  VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, 7, 0, (SELECT QUESTIONNAIRE_REF_ID FROM QUESTIONNAIRE WHERE NAME = 'IRB Long' AND SEQUENCE_NUMBER = 1), 1, 0, 'IRB Questionnaire 1', to_date('2009-12-19 16:08:51','YYYY-MM-DD HH24:MI:SS'), 'admin', 1, SYS_GUID())
/
INSERT INTO QUESTIONNAIRE_USAGE(QUESTIONNAIRE_USAGE_ID, MODULE_ITEM_CODE, MODULE_SUB_ITEM_CODE, QUESTIONNAIRE_REF_ID_FK, QUESTIONNAIRE_SEQUENCE_NUMBER, RULE_ID, QUESTIONNAIRE_LABEL, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID) 
  VALUES(SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, 7, 0, (SELECT QUESTIONNAIRE_REF_ID FROM QUESTIONNAIRE WHERE NAME = 'IRB Long' AND SEQUENCE_NUMBER = 2), 1, 0, 'IRB Questionnaire 1 V2', to_date('2009-12-23 14:17:59','YYYY-MM-DD HH24:MI:SS'), 'admin', 1, SYS_GUID())
/
