CREATE TABLE refresh_tokens(
	 id BIGSERIAL PRIMARY KEY,
	 token VARCHAR(512) NOT NULL UNIQUE,
	 user_id BIGINT NOT NULL,
	expiryDate TIMESTAMPTZ NOT NULL,
	revoked BOOLEAN NOT NULL DEFAULT FALSE,
	
	CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);