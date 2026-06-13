--liquibase formatted sql

--changeset ali:006-add-ticket-analysis-raw-model-output
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'raw_model_output';
ALTER TABLE ticket_analysis
    ADD COLUMN raw_model_output TEXT;

--changeset ali:007-add-ticket-analysis-created-at
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'created_at';
ALTER TABLE ticket_analysis
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

--changeset ali:008-add-ticket-analysis-updated-at
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'ticket_analysis' AND column_name = 'updated_at';
ALTER TABLE ticket_analysis
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;