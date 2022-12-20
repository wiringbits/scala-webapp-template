
-- !Ups

-- Stores the background jobs from the app
CREATE TABLE background_jobs (
    background_job_id UUID NOT NULL,
    type TEXT NOT NULL,
    payload JSONB NOT NULL,
    status TEXT NOT NULL, -- pending/success/failed,
    status_details TEXT NULL, -- if failed, what was the reason
    error_count INT DEFAULT 0,
    execute_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT background_jobs_id_pk PRIMARY KEY (background_job_id)
);

CREATE INDEX background_jobs_execute_at_index ON background_jobs USING BTREE (execute_at);

-- these are now handled by background_jobs
DROP TABLE user_notifications;
