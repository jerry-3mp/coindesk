CREATE TABLE IF NOT EXISTS coins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE coins IS 'Table to store coin information';
COMMENT ON COLUMN coins.name IS 'Name of the cryptocurrency';
