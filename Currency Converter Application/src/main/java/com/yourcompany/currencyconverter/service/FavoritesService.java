package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.DuplicateResourceException;
import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.model.entity.FavoriteCurrency;
import com.yourcompany.currencyconverter.repository.CurrencyRepository;
import com.yourcompany.currencyconverter.repository.FavoriteCurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private final FavoriteCurrencyRepository favoriteRepository;
    private final CurrencyRepository currencyRepository;

    public FavoritesService(FavoriteCurrencyRepository favoriteRepository, CurrencyRepository currencyRepository) {
        this.favoriteRepository = favoriteRepository;
        this.currencyRepository = currencyRepository;
    }

    @Transactional(readOnly = true)
    public List<String> getAllFavorites() {
        return favoriteRepository.findAll()
                .stream()
                .map(fav -> fav.getCurrency().getCode())
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFavorite(String code) {
        if (favoriteRepository.existsByCurrencyCode(code)) {
            throw new DuplicateResourceException("Currency '" + code + "' is already in favorites.");
        }

        Currency currency = currencyRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not supported: '" + code + "'"));

        favoriteRepository.save(new FavoriteCurrency(currency));
    }

    @Transactional
    public void removeFavorite(String code) {
        if (!favoriteRepository.existsByCurrencyCode(code)) {
            throw new ResourceNotFoundException("Currency '" + code + "' is not in favorites.");
        }
        favoriteRepository.deleteByCurrencyCode(code);
    }
}
