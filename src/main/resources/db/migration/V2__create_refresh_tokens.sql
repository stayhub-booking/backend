CREATE TABLE refresh_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL REFERENCES users (id),
    family_id  UUID        NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked    BOOLEAN     NOT NULL DEFAULT FALSE
);