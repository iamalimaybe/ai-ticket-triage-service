--liquibase formatted sql

--changeset ali:002-create-ticket-analysis-missing-information
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'ticket_analysis_missing_information';
CREATE TABLE ticket_analysis_missing_information (
     ticket_analysis_id BIGINT NOT NULL,
     field_name VARCHAR(120) NOT NULL
);