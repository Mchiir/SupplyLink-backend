-- Capture INSERTs
CREATE OR REPLACE FUNCTION audit_products_insert()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (table_name, operation, record_id, new_data, triggered_by)
VALUES (
           'products',
           'INSERT',
           NEW.id,
           row_to_json(NEW),
           current_user
       );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Capture UPDATEs
CREATE OR REPLACE FUNCTION audit_products_update()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (table_name, operation, record_id, old_data, new_data, triggered_by)
VALUES (
           'products',
           'UPDATE',
           NEW.id,
           row_to_json(OLD),
           row_to_json(NEW),
           current_user
       );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Capture DELETEs
CREATE OR REPLACE FUNCTION audit_products_delete()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO audit_logs (table_name, operation, record_id, old_data, triggered_by)
VALUES (
           'products',
           'DELETE',
           OLD.id,
           row_to_json(OLD),
           current_user
       );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;