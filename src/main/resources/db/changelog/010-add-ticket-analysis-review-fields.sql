--liquibase formatted sql

--changeset ali:010-add-ticket-analysis-review-status
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'review_status';
ALTER TABLE ticket_analysis
    ADD COLUMN review_status VARCHAR(40) NOT NULL DEFAULT 'NOT_REQUIRED';

--changeset ali:011-add-ticket-analysis-review-reason
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'review_reason';
ALTER TABLE ticket_analysis
    ADD COLUMN review_reason VARCHAR(500);