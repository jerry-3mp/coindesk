-- English translations
INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'en', name FROM coins WHERE name = 'Bitcoin';

INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'en', name FROM coins WHERE name = 'Ethereum';

INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'en', name FROM coins WHERE name = 'Litecoin';

-- Traditional Chinese (Taiwan) translations
INSERT INTO coin_i18n (coin_id, lang_code, name)
SELECT id, 'zh-TW', '比特幣' FROM coins WHERE name = 'Bitcoin';

INSERT INTO coin_i18n (coin_id, lang_code, name)
SELECT id, 'zh-TW', '以太坊' FROM coins WHERE name = 'Ethereum';

INSERT INTO coin_i18n (coin_id, lang_code, name)
SELECT id, 'zh-TW', '萊特幣' FROM coins WHERE name = 'Litecoin';

-- Japanese translations
INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'ja', 'ビットコイン' FROM coins WHERE name = 'Bitcoin';

INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'ja', 'イーサリアム' FROM coins WHERE name = 'Ethereum';

INSERT INTO coin_i18n (coin_id, lang_code, name) 
SELECT id, 'ja', 'ライトコイン' FROM coins WHERE name = 'Litecoin';
