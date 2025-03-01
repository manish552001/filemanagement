CREATE TABLE IF NOT EXISTS user_files (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    public_url VARCHAR(1000),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT idx_user_files_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_user_files_user_id ON user_files(user_id);