package com.supplylink.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        String sql = """
            CREATE TABLE IF NOT EXISTS audit_logs (
                                        id BIGSERIAL PRIMARY KEY,
                                        table_name VARCHAR(100) NOT NULL,
                                        operation VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
                                        record_id UUID NOT NULL,
                                        old_data JSONB,
                                        new_data JSONB,
                                        triggered_by VARCHAR(255), -- could be username or user_id
                                        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        
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
            
           -- Attach triggers to the products table
           DROP TRIGGER IF EXISTS trg_audit_products_insert ON products;
           CREATE TRIGGER trg_audit_products_insert
               AFTER INSERT ON products
               FOR EACH ROW EXECUTE FUNCTION audit_products_insert();
           
           DROP TRIGGER IF EXISTS trg_audit_products_update ON products;
           CREATE TRIGGER trg_audit_products_update
               AFTER UPDATE ON products
               FOR EACH ROW EXECUTE FUNCTION audit_products_update();
           
           DROP TRIGGER IF EXISTS trg_audit_products_delete ON products;
           CREATE TRIGGER trg_audit_products_delete
               AFTER DELETE ON products
               FOR EACH ROW EXECUTE FUNCTION audit_products_delete();

            CREATE INDEX IF NOT EXISTS idx_audit_table_record ON audit_logs (table_name, record_id);
        """;

        // Execute the SQL script
        jdbcTemplate.execute(sql);
    }
}
