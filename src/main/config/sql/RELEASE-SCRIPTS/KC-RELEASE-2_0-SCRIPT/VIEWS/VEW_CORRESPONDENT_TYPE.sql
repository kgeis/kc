-- View for Coeus compatibility  
CREATE OR REPLACE VIEW OSP$CORRESPONDENT_TYPE AS 
SELECT CORRESPONDENT_TYPE_CODE,
       DESCRIPTION,
       QUALIFIER,
       UPDATE_TIMESTAMP,
       UPDATE_USER
FROM   CORRESPONDENT_TYPE;