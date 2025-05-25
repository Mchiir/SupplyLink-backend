CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            table_name VARCHAR(100) NOT NULL,
                            operation VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
                            record_id BIGINT NOT NULL,
                            old_data JSONB,
                            new_data JSONB,
                            triggered_by VARCHAR(255), -- could be username or user_id
                            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);