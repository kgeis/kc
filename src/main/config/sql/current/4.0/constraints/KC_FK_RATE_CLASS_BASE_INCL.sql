ALTER TABLE RATE_CLASS_BASE_INCLUSION 
ADD CONSTRAINT FK_RATE_CLASS_BASE_INCL 
FOREIGN KEY (RATE_CLASS_CODE,RATE_TYPE_CODE) 
REFERENCES RATE_TYPE (RATE_CLASS_CODE,RATE_TYPE_CODE)
/
