package com.yourcompany.currencyconverter.controller;

import com.yourcompany.currencyconverter.model.dto.FavoriteRequest;
import com.yourcompany.currencyconverter.service.FavoritesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getFavorites() {
        return ResponseEntity.ok(favoritesService.getAllFavorites());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        favoritesService.addFavorite(request.getCurrency());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Added", "currency", request.getCurrency()));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String code) {
        favoritesService.removeFavorite(code.toUpperCase());
        return ResponseEntity.noContent().build();
    }
}
