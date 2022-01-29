
-- !Ups


-- The users table has the minimum necessary data
CREATE TABLE users(
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  last_name TEXT NULL,
  email CITEXT NOT NULL,
  password TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  verified_on TIMESTAMPTZ NULL,
  CONSTRAINT users_user_id_pk PRIMARY KEY (user_id),
  CONSTRAINT users_email_unique UNIQUE (email)
);

CREATE INDEX users_email_index ON users USING BTREE (email);

-- create the table to store the user logs
CREATE TABLE user_logs (
    user_log_id UUID NOT NULL,
    user_id UUID NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT user_logs_pk PRIMARY KEY (user_log_id),
    CONSTRAINT user_logs_users_fk FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX user_logs_user_id_index ON user_logs USING BTREE (user_id);

CREATE TABLE user_tokens (
    user_token_id UUID NOT NULL,
    token TEXT NOT NULL,
    token_type TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT user_tokens_id_pk PRIMARY KEY (user_token_id),
    CONSTRAINT user_tokens_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE INDEX user_tokens_user_id_index ON user_tokens USING BTREE (user_id);

-- Stores the notifications we are sending to the user from a background job
CREATE TABLE user_notifications (
    user_notification_id UUID NOT NULL,
    user_id UUID NOT NULL,
    notification_type TEXT NOT NULL,
    subject TEXT NOT NULL,
    message TEXT NOT NULL,
    status TEXT NOT NULL, -- pending/success/failed,
    status_details TEXT NULL, -- if failed, what was the reason
    error_count INT DEFAULT 0,
    execute_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT user_notifications_user_notification_id_pk PRIMARY KEY (user_notification_id),
    CONSTRAINT user_notifications_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX user_notifications_user_id_index ON user_notifications USING BTREE (user_id);
CREATE INDEX user_notifications_execute_at_index ON user_notifications USING BTREE (execute_at);