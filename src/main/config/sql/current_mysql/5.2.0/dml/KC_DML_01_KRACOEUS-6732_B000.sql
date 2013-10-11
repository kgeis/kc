DELIMITER /

INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (11,'Administrative Approval',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Administrative Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Administrative Approval'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (12,'Administratively Incomplete',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Administrative Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Administratively Incomplete'))
/

INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (13,'Major Revisions Required',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Full Committee Member Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Major Revisions Required'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (14,'Minor Revisions Required',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Full Committee Member Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Minor Revisions Required'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (15,'Approve',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Full Committee Member Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Approved'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (16,'Disapprove',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Full Committee Member Review'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Disapproved'))
/

INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (17,'IACUC Acknowledgement',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='FYI'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='IACUC  Acknowledgement'))
/

INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (18,'Major Revisions Required',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Response'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Major Revisions Required'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (19,'Minor Revisions Required',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Response'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Minor Revisions Required'))
/
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (20,'Response Approval',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='Response'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='Response Approval'))
/
	
INSERT INTO IACUC_PROTO_OLR_DT_REC_TYPE (REVIEW_DETERM_RECOM_CD,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,ASSOC_REVIEW_TYPE,IACUC_PROT_ACTION_TYPE)
       VALUES (21,'IACUC Review Not Required',SYSDATE(),'admin',1,uuid(),(SELECT PROTOCOL_REVIEW_TYPE_CODE FROM IACUC_PROTO_REVIEW_TYPE WHERE DESCRIPTION='IACUC Review not required'),(SELECT PROTOCOL_ACTION_TYPE_CODE FROM IACUC_PROTOCOL_ACTION_TYPE WHERE DESCRIPTION='IACUC review not required'))
/

DELIMITER ;
       