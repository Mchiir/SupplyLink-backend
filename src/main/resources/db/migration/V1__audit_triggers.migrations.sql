-- Attach triggers to the products table
CREATE TRIGGER trg_audit_products_insert
    AFTER INSERT ON products
    FOR EACH ROW EXECUTE FUNCTION audit_products_insert();

CREATE TRIGGER trg_audit_products_update
    AFTER UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION audit_products_update();

CREATE TRIGGER trg_audit_products_delete
    AFTER DELETE ON products
    FOR EACH ROW EXECUTE FUNCTION audit_products_delete();