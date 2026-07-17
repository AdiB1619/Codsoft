package com.yourcompany.currencyconverter.repository;

import com.yourcompany.currencyconverter.model.entity.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CurrencyRepository}.
 *
 * <p>Uses {@link DataJpaTest} which:
 * <ul>
 *   <li>Spins up an H2 in-memory database (configured via {@code src/test/resources/application.properties}).</li>
 *   <li>Loads only JPA-related beans — controllers and services are NOT loaded, keeping tests fast.</li>
 *   <li>Wraps each test in a transaction that is rolled back after the test, ensuring isolation.</li>
 * </ul>
 *
 * <p>Covers the acceptance criteria:
 * <ul>
 *   <li>Save a {@code Currency} and retrieve it by ID.</li>
 *   <li>Find all currencies.</li>
 *   <li>Check existence by code.</li>
 *   <li>Search by partial name (case-insensitive).</li>
 *   <li>Delete a currency.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CurrencyRepository – Integration Tests")
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    // -------------------------------------------------------------------------
    // save + findById (Primary acceptance-criteria test)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save(Currency) then findById returns the persisted entity")
    void saveAndFindById_shouldReturnSavedCurrency() {
        // Arrange
        Currency usd = new Currency("USD", "US Dollar", "$");

        // Act
        currencyRepository.save(usd);
        Optional<Currency> found = currencyRepository.findById("USD");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("USD");
        assertThat(found.get().getName()).isEqualTo("US Dollar");
        assertThat(found.get().getSymbol()).isEqualTo("$");
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns all saved currencies")
    void findAll_shouldReturnAllSavedCurrencies() {
        // Arrange
        currencyRepository.save(new Currency("USD", "US Dollar", "$"));
        currencyRepository.save(new Currency("EUR", "Euro", "€"));
        currencyRepository.save(new Currency("JPY", "Japanese Yen", "¥"));

        // Act
        List<Currency> all = currencyRepository.findAll();

        // Assert
        assertThat(all).hasSize(3);
        assertThat(all).extracting(Currency::getCode)
                .containsExactlyInAnyOrder("USD", "EUR", "JPY");
    }

    // -------------------------------------------------------------------------
    // existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("existsById returns true for a saved currency code")
    void existsById_shouldReturnTrueForSavedCode() {
        currencyRepository.save(new Currency("GBP", "British Pound", "£"));

        assertThat(currencyRepository.existsById("GBP")).isTrue();
        assertThat(currencyRepository.existsById("XYZ")).isFalse();
    }

    // -------------------------------------------------------------------------
    // findByNameContainingIgnoreCase
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByNameContainingIgnoreCase matches partial and case-insensitive names")
    void findByNameContainingIgnoreCase_shouldMatchPartialName() {
        currencyRepository.save(new Currency("USD", "US Dollar", "$"));
        currencyRepository.save(new Currency("CAD", "Canadian Dollar", "CA$"));
        currencyRepository.save(new Currency("EUR", "Euro", "€"));

        List<Currency> dollarCurrencies = currencyRepository.findByNameContainingIgnoreCase("dollar");

        assertThat(dollarCurrencies).hasSize(2);
        assertThat(dollarCurrencies).extracting(Currency::getCode)
                .containsExactlyInAnyOrder("USD", "CAD");
    }

    // -------------------------------------------------------------------------
    // findById – not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById returns empty Optional for unknown currency code")
    void findById_shouldReturnEmptyForUnknownCode() {
        Optional<Currency> result = currencyRepository.findById("XYZ");

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById removes the currency from the database")
    void deleteById_shouldRemoveCurrency() {
        currencyRepository.save(new Currency("CHF", "Swiss Franc", "Fr"));
        assertThat(currencyRepository.existsById("CHF")).isTrue();

        currencyRepository.deleteById("CHF");

        assertThat(currencyRepository.existsById("CHF")).isFalse();
    }

    // -------------------------------------------------------------------------
    // equals / hashCode contract
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Two Currency objects with the same code are equal")
    void equals_shouldBeTrueForSameCode() {
        Currency a = new Currency("EUR", "Euro", "€");
        Currency b = new Currency("EUR", "Euro", "€");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
