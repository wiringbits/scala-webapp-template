
-- !Ups

CREATE EXTENSION IF NOT EXISTS CITEXT;

-- The users table has the minimum necessary data
CREATE TABLE users(
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  last_name TEXT NULL,
  email CITEXT NOT NULL,
  password TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
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
