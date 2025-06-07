package com.supplylink.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
                operation VARCHAR(10) NOT NULL,
                record_id BIGINT NOT NULL,
                old_data JSONB,
                new_data JSONB,
                triggered_by VARCHAR(255),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            -- INSERT trigger function
            CREATE OR REPLACE FUNCTION audit_products_insert()
            RETURNS TRIGGER AS $$
            BEGIN
                INSERT INTO audit_logs (
                    table_name, operation, record_id, new_data, triggered_by, timestamp
                )
                VALUES (
                    'products', 'INSERT', NEW.id, to_jsonb(NEW), current_user, CURRENT_TIMESTAMP
                );
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;
            
            -- UPDATE trigger function
            CREATE OR REPLACE FUNCTION audit_products_update()
            RETURNS TRIGGER AS $$
            BEGIN
                INSERT INTO audit_logs (
                    table_name, operation, record_id, old_data, new_data, triggered_by, timestamp
                )
                VALUES (
                    'products', 'UPDATE', NEW.id, to_jsonb(OLD), to_jsonb(NEW), current_user, CURRENT_TIMESTAMP
                );
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;
            
            -- DELETE trigger function
            CREATE OR REPLACE FUNCTION audit_products_delete()
            RETURNS TRIGGER AS $$
            BEGIN
                INSERT INTO audit_logs (
                    table_name, operation, record_id, old_data, triggered_by, timestamp
                )
                VALUES (
                    'products', 'DELETE', OLD.id, to_jsonb(OLD), current_user, CURRENT_TIMESTAMP
                );
                RETURN OLD;
            END;
            $$ LANGUAGE plpgsql;
            
            -- Triggers
            CREATE TRIGGER trg_audit_products_insert
            AFTER INSERT ON products
            FOR EACH ROW EXECUTE FUNCTION audit_products_insert();
            
            CREATE TRIGGER trg_audit_products_update
            AFTER UPDATE ON products
            FOR EACH ROW EXECUTE FUNCTION audit_products_update();
            
            CREATE TRIGGER trg_audit_products_delete
            AFTER DELETE ON products
            FOR EACH ROW EXECUTE FUNCTION audit_products_delete();
            
            -- Index
            CREATE INDEX IF NOT EXISTS idx_audit_table_record ON audit_logs (table_name, record_id);
        """;

        // Execute the SQL script
        jdbcTemplate.execute(sql);
    }
}