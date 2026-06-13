--liquibase formatted sql

--changeset ali:003-add-ticket-analysis-missing-information-fk
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name = 'fk_missing_information_ticket_analysis';
ALTER TABLE ticket_analysis_missing_information
    ADD CONSTRAINT fk_missing_information_ticket_analysis
        FOREIGN KEY (ticket_analysis_id)
            REFERENCES ticket_analysis(id)
            ON DELETE CASCADE;