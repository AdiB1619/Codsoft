-- =============================================================================
-- Currency Converter Application – Seed Data
-- =============================================================================
-- Populates the `currencies` table with ISO 4217 standard currency codes,
-- names, and symbols.
--
-- Strategy:
--   INSERT IGNORE is used so that re-running this file on an already-populated
--   database (e.g. after restart with ddl-auto=update) is completely safe.
--   Existing rows are left unchanged; only missing ones are inserted.
--
-- Spring Boot automatically executes this file on startup when:
--   - spring.jpa.defer-datasource-initialization=true (set in application.properties)
--   - spring.sql.init.mode=always (default for embedded DBs) or "always" explicit
-- =============================================================================

-- ── Americas ──────────────────────────────────────────────────────────────────
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('USD', 'US Dollar',               '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CAD', 'Canadian Dollar',         'CA$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MXN', 'Mexican Peso',            '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BRL', 'Brazilian Real',          'R$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ARS', 'Argentine Peso',          '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CLP', 'Chilean Peso',            '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('COP', 'Colombian Peso',          '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PEN', 'Peruvian Sol',            'S/');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('UYU', 'Uruguayan Peso',          '$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BOB', 'Bolivian Boliviano',      'Bs.');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PYG', 'Paraguayan Guaraní',      '₲');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('VES', 'Venezuelan Bolívar',      'Bs.S');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('GTQ', 'Guatemalan Quetzal',      'Q');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CRC', 'Costa Rican Colón',       '₡');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('DOP', 'Dominican Peso',          'RD$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('TTD', 'Trinidad & Tobago Dollar','TT$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('JMD', 'Jamaican Dollar',         'J$');

-- ── Europe ────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('EUR', 'Euro',                    '€');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('GBP', 'British Pound Sterling',  '£');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CHF', 'Swiss Franc',             'Fr');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('NOK', 'Norwegian Krone',         'kr');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('SEK', 'Swedish Krona',           'kr');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('DKK', 'Danish Krone',            'kr');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PLN', 'Polish Zloty',            'zł');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CZK', 'Czech Koruna',            'Kč');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('HUF', 'Hungarian Forint',        'Ft');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('RON', 'Romanian Leu',            'lei');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BGN', 'Bulgarian Lev',           'лв');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('HRK', 'Croatian Kuna',           'kn');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('RSD', 'Serbian Dinar',           'din');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ISK', 'Icelandic Króna',         'kr');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('UAH', 'Ukrainian Hryvnia',       '₴');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('RUB', 'Russian Ruble',           '₽');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('TRY', 'Turkish Lira',            '₺');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MDL', 'Moldovan Leu',            'lei');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ALL', 'Albanian Lek',            'L');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MKD', 'Macedonian Denar',        'ден');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BAM', 'Bosnian Mark',            'KM');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('GEL', 'Georgian Lari',           '₾');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AMD', 'Armenian Dram',           '֏');

-- ── Asia & Oceania ────────────────────────────────────────────────────────────
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('JPY', 'Japanese Yen',            '¥');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('CNY', 'Chinese Yuan Renminbi',   '¥');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('INR', 'Indian Rupee',            '₹');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('KRW', 'South Korean Won',        '₩');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('IDR', 'Indonesian Rupiah',       'Rp');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MYR', 'Malaysian Ringgit',       'RM');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('SGD', 'Singapore Dollar',        'S$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('THB', 'Thai Baht',               '฿');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PHP', 'Philippine Peso',         '₱');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('VND', 'Vietnamese Dong',         '₫');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('TWD', 'New Taiwan Dollar',       'NT$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('HKD', 'Hong Kong Dollar',        'HK$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MOP', 'Macanese Pataca',         'P');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BDT', 'Bangladeshi Taka',        '৳');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PKR', 'Pakistani Rupee',         '₨');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('LKR', 'Sri Lankan Rupee',        'Rs');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('NPR', 'Nepalese Rupee',          'Rs');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MMK', 'Myanmar Kyat',            'K');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('KHR', 'Cambodian Riel',          '៛');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('LAK', 'Lao Kip',                 '₭');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MNT', 'Mongolian Tögrög',        '₮');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AZN', 'Azerbaijani Manat',       '₼');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('KZT', 'Kazakhstani Tenge',       '₸');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('UZS', 'Uzbekistani Som',         'so\'m');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AFN', 'Afghan Afghani',          '؋');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('IRR', 'Iranian Rial',            '﷼');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('IQD', 'Iraqi Dinar',             'ع.د');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('SAR', 'Saudi Riyal',             '﷼');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AED', 'UAE Dirham',              'د.إ');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('QAR', 'Qatari Riyal',            '﷼');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('KWD', 'Kuwaiti Dinar',           'د.ك');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BHD', 'Bahraini Dinar',          'BD');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('OMR', 'Omani Rial',              'ر.ع.');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('JOD', 'Jordanian Dinar',         'JD');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('LBP', 'Lebanese Pound',          'L£');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('SYP', 'Syrian Pound',            '£');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('YER', 'Yemeni Rial',             '﷼');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ILS', 'Israeli New Shekel',      '₪');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AUD', 'Australian Dollar',       'A$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('NZD', 'New Zealand Dollar',      'NZ$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('FJD', 'Fijian Dollar',           'FJ$');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('PGK', 'Papua New Guinean Kina',  'K');

-- ── Africa ────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ZAR', 'South African Rand',      'R');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('NGN', 'Nigerian Naira',          '₦');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('EGP', 'Egyptian Pound',          'E£');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('KES', 'Kenyan Shilling',         'KSh');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('GHS', 'Ghanaian Cedi',           '₵');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('TZS', 'Tanzanian Shilling',      'TSh');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('UGX', 'Ugandan Shilling',        'USh');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ETB', 'Ethiopian Birr',          'Br');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MAD', 'Moroccan Dirham',         'MAD');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('DZD', 'Algerian Dinar',          'دج');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('TND', 'Tunisian Dinar',          'DT');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('XOF', 'West African CFA Franc',  'CFA');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('XAF', 'Central African CFA Franc','FCFA');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MZN', 'Mozambican Metical',      'MT');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('ZMW', 'Zambian Kwacha',          'ZK');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('AOA', 'Angolan Kwanza',          'Kz');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('SDG', 'Sudanese Pound',          'ج.س.');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('RWF', 'Rwandan Franc',           'RF');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('BWP', 'Botswanan Pula',          'P');
INSERT IGNORE INTO currencies (code, name, symbol) VALUES ('MUR', 'Mauritian Rupee',         '₨');
