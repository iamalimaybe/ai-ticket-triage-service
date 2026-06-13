--liquibase formatted sql

--changeset ali:009-add-ticket-analysis-model-confidence
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'model_confidence';
ALTER TABLE ticket_analysis
    ADD COLUMN model_confidence DOUBLE PRECISION;