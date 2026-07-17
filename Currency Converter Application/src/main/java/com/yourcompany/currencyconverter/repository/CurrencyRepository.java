package com.yourcompany.currencyconverter.repository;

import com.yourcompany.currencyconverter.model.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Currency} entities.
 *
 * <p>Extends {@link JpaRepository} with {@code String} as the ID type (matching the
 * ISO 4217 code primary key). Spring Boot auto-generates the implementation at
 * startup — no boilerplate SQL needed.
 *
 * <p>Inherited methods of immediate relevance:
 * <ul>
 *   <li>{@code save(Currency)} – insert or update a currency record.</li>
 *   <li>{@code findById(String)} – look up a currency by its ISO code.</li>
 *   <li>{@code findAll()} – retrieve all supported currencies (used by the
 *       {@code GET /api/currencies} endpoint).</li>
 *   <li>{@code existsById(String)} – validate that a currency code is supported
 *       (called during conversion request validation).</li>
 *   <li>{@code deleteById(String)} – remove a currency if needed.</li>
 * </ul>
 *
 * <p>Custom query methods can be added here as the application grows (e.g.
 * {@code findByNameContainingIgnoreCase(String)} for search/filter functionality).
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    /**
     * Case-insensitive name search — supports the currency search/filter feature
     * described in SDD §2 ("Search/Filter Currencies").
     *
     * @param name partial or full currency name to search
     * @return list of matching currencies
     */
    java.util.List<Currency> findByNameContainingIgnoreCase(String name);
}
