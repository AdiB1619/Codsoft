package com.yourcompany.currencyconverter.repository;

import com.yourcompany.currencyconverter.model.entity.FavoriteCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteCurrencyRepository extends JpaRepository<FavoriteCurrency, Long> {
    
    /**
     * Finds a favorite record by the currency code.
     */
    Optional<FavoriteCurrency> findByCurrencyCode(String code);

    /**
     * Checks if a currency is already in favorites.
     */
    boolean existsByCurrencyCode(String code);

    /**
     * Deletes a favorite record by the currency code.
     */
    void deleteByCurrencyCode(String code);
}
