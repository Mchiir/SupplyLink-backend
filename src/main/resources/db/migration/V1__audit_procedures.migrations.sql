-- Capture INSERTs
CREATE OR REPLACE FUNCTION audit_products_insert()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (
    table_name,
    operation,
    record_id,
    new_data,
    triggered_by,
    timestamp
)
VALUES (
           'products',
           'INSERT',
           NEW.id,
           to_jsonb(NEW),
           current_user,
           CURRENT_TIMESTAMP
       );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Capture UPDATEs
CREATE OR REPLACE FUNCTION audit_products_update()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (
    table_name,
    operation,
    record_id,
    old_data,
    new_data,
    triggered_by,
    timestamp
)
VALUES (
           'products',
           'UPDATE',
           NEW.id,
           to_jsonb(OLD),
           to_jsonb(NEW),
           current_user,
           CURRENT_TIMESTAMP
       );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Capture DELETEs
CREATE OR REPLACE FUNCTION audit_products_delete()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (
    table_name,
    operation,
    record_id,
    old_data,
    triggered_by,
    timestamp
)
VALUES (
           'products',
           'DELETE',
           OLD.id,
           to_jsonb(OLD),
           current_user,
           CURRENT_TIMESTAMP
       );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;