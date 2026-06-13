--liquibase formatted sql

--changeset ali:012-add-ticket-analysis-reviewed-at
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'reviewed_at';
ALTER TABLE ticket_analysis
    ADD COLUMN reviewed_at TIMESTAMP WITH TIME ZONE;

--changeset ali:013-add-ticket-analysis-reviewed-by
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'reviewed_by';
ALTER TABLE ticket_analysis
    ADD COLUMN reviewed_by VARCHAR(120);