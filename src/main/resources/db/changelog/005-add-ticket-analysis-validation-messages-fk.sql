--liquibase formatted sql

--changeset ali:005-add-ticket-analysis-validation-messages-fk
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name = 'fk_validation_messages_ticket_analysis';
ALTER TABLE ticket_analysis_validation_messages
    ADD CONSTRAINT fk_validation_messages_ticket_analysis
        FOREIGN KEY (ticket_analysis_id)
            REFERENCES ticket_analysis(id)
            ON DELETE CASCADE;