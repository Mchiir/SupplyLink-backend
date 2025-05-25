-- Index for faster lookup
CREATE INDEX idx_audit_table_record ON audit_logs (table_name, record_id);