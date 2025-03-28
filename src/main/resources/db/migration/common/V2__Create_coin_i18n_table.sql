CREATE TABLE IF NOT EXISTS coin_i18n (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coin_id BIGINT NOT NULL,
    lang_code VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coin_id) REFERENCES coins(id),
    CONSTRAINT unique_coin_lang UNIQUE (coin_id, lang_code)
);

COMMENT ON TABLE coin_i18n IS 'Table to store internationalized coin names';
COMMENT ON COLUMN coin_i18n.coin_id IS 'Reference to the coin ID';
COMMENT ON COLUMN coin_i18n.lang_code IS 'Language code (e.g., en, es, fr)';
COMMENT ON COLUMN coin_i18n.name IS 'Localized name of the cryptocurrency';
