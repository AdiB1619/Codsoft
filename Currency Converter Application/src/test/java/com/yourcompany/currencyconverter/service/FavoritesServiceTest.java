package com.yourcompany.currencyconverter.service;

import com.yourcompany.currencyconverter.exception.DuplicateResourceException;
import com.yourcompany.currencyconverter.exception.ResourceNotFoundException;
import com.yourcompany.currencyconverter.model.entity.Currency;
import com.yourcompany.currencyconverter.model.entity.FavoriteCurrency;
import com.yourcompany.currencyconverter.repository.CurrencyRepository;
import com.yourcompany.currencyconverter.repository.FavoriteCurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceTest {

    @Mock
    private FavoriteCurrencyRepository favoriteRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private FavoritesService favoritesService;

    private Currency usdCurrency;
    private FavoriteCurrency favoriteUsd;

    @BeforeEach
    void setUp() {
        usdCurrency = new Currency("USD", "US Dollar", "$");
        favoriteUsd = new FavoriteCurrency(usdCurrency);
    }

    @Test
    void getAllFavorites_ReturnsListOfCodes() {
        when(favoriteRepository.findAll()).thenReturn(List.of(favoriteUsd));

        List<String> favorites = favoritesService.getAllFavorites();

        assertEquals(1, favorites.size());
        assertEquals("USD", favorites.get(0));
        verify(favoriteRepository).findAll();
    }

    @Test
    void addFavorite_WhenValidAndNotExists_SavesSuccessfully() {
        when(favoriteRepository.existsByCurrencyCode("USD")).thenReturn(false);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(usdCurrency));

        favoritesService.addFavorite("USD");

        verify(favoriteRepository).save(any(FavoriteCurrency.class));
    }

    @Test
    void addFavorite_WhenAlreadyExists_ThrowsDuplicateResourceException() {
        when(favoriteRepository.existsByCurrencyCode("EUR")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> favoritesService.addFavorite("EUR"));

        verify(favoriteRepository, never()).save(any(FavoriteCurrency.class));
    }

    @Test
    void addFavorite_WhenCurrencyNotFound_ThrowsResourceNotFoundException() {
        when(favoriteRepository.existsByCurrencyCode("XYZ")).thenReturn(false);
        when(currencyRepository.findById("XYZ")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favoritesService.addFavorite("XYZ"));

        verify(favoriteRepository, never()).save(any(FavoriteCurrency.class));
    }

    @Test
    void removeFavorite_WhenExists_DeletesSuccessfully() {
        when(favoriteRepository.existsByCurrencyCode("USD")).thenReturn(true);

        favoritesService.removeFavorite("USD");

        verify(favoriteRepository).deleteByCurrencyCode("USD");
    }

    @Test
    void removeFavorite_WhenNotExists_ThrowsResourceNotFoundException() {
        when(favoriteRepository.existsByCurrencyCode("GBP")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> favoritesService.removeFavorite("GBP"));

        verify(favoriteRepository, never()).deleteByCurrencyCode(anyString());
    }
}
