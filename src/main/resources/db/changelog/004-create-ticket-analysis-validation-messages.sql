--liquibase formatted sql

--changeset ali:004-create-ticket-analysis-validation-messages
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'ticket_analysis_validation_messages';
CREATE TABLE ticket_analysis_validation_messages (
     ticket_analysis_id BIGINT NOT NULL,
     message VARCHAR(500) NOT NULL
);