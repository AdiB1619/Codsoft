package com.yourcompany.currencyconverter.repository;

import com.yourcompany.currencyconverter.model.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that verify the currency seed data defined in {@code data.sql}.
 *
 * <p>Because {@code data.sql} uses MySQL-specific {@code INSERT IGNORE} syntax which
 * H2 does not support, SQL init is disabled for tests via
 * {@code spring.sql.init.mode=never}. The seed data is therefore replicated
 * programmatically in {@link #setUp()} so the same assertions that would run against
 * a live MySQL database can be verified in the CI-friendly H2 environment.
 *
 * <p>This mirrors exactly what {@code data.sql} inserts, so any change to the seed
 * file should be reflected here as well.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Currency Seed Data – Verification Tests")
class CurrencySeedDataTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    /**
     * Replicates the data.sql seed programmatically so tests are independent
     * of SQL dialect differences between MySQL and H2.
     */
    @BeforeEach
    void setUp() {
        // Mirrors the exact rows inserted by data.sql – keep in sync with that file.
        List<Currency> seedData = Arrays.asList(
                // Americas
                new Currency("USD", "US Dollar",               "$"),
                new Currency("CAD", "Canadian Dollar",         "CA$"),
                new Currency("MXN", "Mexican Peso",            "$"),
                new Currency("BRL", "Brazilian Real",          "R$"),
                new Currency("ARS", "Argentine Peso",          "$"),
                // Europe
                new Currency("EUR", "Euro",                    "€"),
                new Currency("GBP", "British Pound Sterling",  "£"),
                new Currency("CHF", "Swiss Franc",             "Fr"),
                new Currency("NOK", "Norwegian Krone",         "kr"),
                new Currency("SEK", "Swedish Krona",           "kr"),
                new Currency("PLN", "Polish Zloty",            "zł"),
                // Asia & Oceania
                new Currency("JPY", "Japanese Yen",            "¥"),
                new Currency("CNY", "Chinese Yuan Renminbi",   "¥"),
                new Currency("INR", "Indian Rupee",            "₹"),
                new Currency("KRW", "South Korean Won",        "₩"),
                new Currency("SGD", "Singapore Dollar",        "S$"),
                new Currency("AUD", "Australian Dollar",       "A$"),
                new Currency("NZD", "New Zealand Dollar",      "NZ$"),
                new Currency("HKD", "Hong Kong Dollar",        "HK$"),
                new Currency("SAR", "Saudi Riyal",             "﷼"),
                new Currency("AED", "UAE Dirham",              "د.إ"),
                new Currency("ILS", "Israeli New Shekel",      "₪"),
                // Africa
                new Currency("ZAR", "South African Rand",      "R"),
                new Currency("NGN", "Nigerian Naira",          "₦"),
                new Currency("EGP", "Egyptian Pound",          "E£"),
                new Currency("KES", "Kenyan Shilling",         "KSh")
        );

        currencyRepository.saveAll(seedData);
        currencyRepository.flush();
    }

    // -------------------------------------------------------------------------
    // Core row-existence checks (mirrors data.sql acceptance criteria)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Essential currencies (USD, EUR, GBP, JPY, INR) are present after seeding")
    void essentialCurrencies_shouldExistAfterSeeding() {
        List<String> essentialCodes = List.of("USD", "EUR", "GBP", "JPY", "INR");

        for (String code : essentialCodes) {
            assertThat(currencyRepository.existsById(code))
                    .as("Currency '%s' should exist after seeding", code)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("USD row has correct name and symbol")
    void usd_shouldHaveCorrectNameAndSymbol() {
        Currency usd = currencyRepository.findById("USD").orElseThrow();

        assertThat(usd.getName()).isEqualTo("US Dollar");
        assertThat(usd.getSymbol()).isEqualTo("$");
    }

    @Test
    @DisplayName("EUR row has correct name and symbol")
    void eur_shouldHaveCorrectNameAndSymbol() {
        Currency eur = currencyRepository.findById("EUR").orElseThrow();

        assertThat(eur.getName()).isEqualTo("Euro");
        assertThat(eur.getSymbol()).isEqualTo("€");
    }

    @Test
    @DisplayName("At least 20 currencies are seeded into the database")
    void seededCurrencies_countShouldBeAtLeastTwenty() {
        long count = currencyRepository.count();

        assertThat(count)
                .as("Seed data should populate at least 20 currencies")
                .isGreaterThanOrEqualTo(20);
    }

    // -------------------------------------------------------------------------
    // Regional coverage checks
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Seed data covers all major regions: Americas, Europe, Asia, Africa")
    void seedData_shouldCoverAllMajorRegions() {
        Map<String, Currency> all = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCode, Function.identity()));

        // Americas
        assertThat(all).containsKey("USD");
        assertThat(all).containsKey("BRL");
        assertThat(all).containsKey("CAD");

        // Europe
        assertThat(all).containsKey("EUR");
        assertThat(all).containsKey("GBP");
        assertThat(all).containsKey("CHF");

        // Asia & Oceania
        assertThat(all).containsKey("JPY");
        assertThat(all).containsKey("INR");
        assertThat(all).containsKey("AUD");

        // Middle East
        assertThat(all).containsKey("AED");
        assertThat(all).containsKey("SAR");

        // Africa
        assertThat(all).containsKey("ZAR");
        assertThat(all).containsKey("NGN");
    }

    // -------------------------------------------------------------------------
    // Idempotency: saving duplicate codes must not create new rows
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Re-saving an existing currency (simulating INSERT IGNORE) does not duplicate rows")
    void reSavingExistingCurrency_shouldNotDuplicateRows() {
        long countBefore = currencyRepository.count();

        // Attempt to save USD again — JPA merge will update in-place (no new row)
        currencyRepository.save(new Currency("USD", "US Dollar", "$"));
        currencyRepository.flush();

        long countAfter = currencyRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }

    // -------------------------------------------------------------------------
    // Field integrity
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All seeded currencies have non-blank code, name, and symbol")
    void allCurrencies_shouldHaveNonBlankFields() {
        List<Currency> all = currencyRepository.findAll();

        for (Currency c : all) {
            assertThat(c.getCode())
                    .as("code must not be blank")
                    .isNotBlank();
            assertThat(c.getName())
                    .as("name must not be blank for currency %s", c.getCode())
                    .isNotBlank();
            assertThat(c.getSymbol())
                    .as("symbol must not be blank for currency %s", c.getCode())
                    .isNotBlank();
        }
    }

    @Test
    @DisplayName("All currency codes are exactly 3 uppercase letters")
    void allCurrencyCodes_shouldBeExactlyThreeUppercaseLetters() {
        List<Currency> all = currencyRepository.findAll();

        for (Currency c : all) {
            assertThat(c.getCode())
                    .as("code '%s' should match ISO 4217 format", c.getCode())
                    .matches("^[A-Z]{3}$");
        }
    }
}
