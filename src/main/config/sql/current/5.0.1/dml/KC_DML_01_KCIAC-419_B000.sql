DELETE FROM IACUC_PROTO_ATTACHMENT_STATUS WHERE DESCRIPTION IN ('Draft', 'Finalized', 'Deleted', 'Superceded', 'Expired')
/
INSERT INTO IACUC_PROTO_ATTACHMENT_STATUS (STATUS_CD, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID)
VALUES (1, 'Complete', SYSDATE,'admin', 1, SYS_GUID())
/
INSERT INTO IACUC_PROTO_ATTACHMENT_STATUS (STATUS_CD, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID)
VALUES (2, 'Incomplete', SYSDATE,'admin', 1, SYS_GUID())
/