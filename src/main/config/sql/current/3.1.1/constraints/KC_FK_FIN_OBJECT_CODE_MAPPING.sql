ALTER TABLE FIN_OBJECT_CODE_MAPPING
  ADD CONSTRAINT FK_UNIT_NUMBER FOREIGN KEY (UNIT_NUMBER)
  REFERENCES UNIT (UNIT_NUMBER);

ALTER TABLE FIN_OBJECT_CODE_MAPPING
  ADD CONSTRAINT FK_RATE_TYPE_CODE FOREIGN KEY (RATE_TYPE_CODE, RATE_CLASS_CODE)
  REFERENCES RATE_TYPE (RATE_TYPE_CODE, RATE_CLASS_CODE);
  
ALTER TABLE FIN_OBJECT_CODE_MAPPING
  ADD CONSTRAINT FK_ACTIVITY_TYPE FOREIGN KEY (ACTIVITY_TYPE_CODE)
  REFERENCES ACTIVITY_TYPE (ACTIVITY_TYPE_CODE);