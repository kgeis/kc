DELIMITER /

ALTER TABLE COI_DISCLOSURE_NOTEPAD
    ADD CONSTRAINT FK_NOTE_TYPE_CODE FOREIGN KEY (COI_NOTEPAD_ENTRY_TYPE_CODE)
    REFERENCES COI_NOTE_TYPE (NOTE_TYPE_CODE)    
/

DELIMITER ;
