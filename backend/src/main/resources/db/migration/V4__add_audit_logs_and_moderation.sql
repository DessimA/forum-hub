-- V4: Add moderation fields to users table.
-- audit_logs table was already created in V1 with UUID-compatible types.

ALTER TABLE users ADD COLUMN suspension_justification TEXT;