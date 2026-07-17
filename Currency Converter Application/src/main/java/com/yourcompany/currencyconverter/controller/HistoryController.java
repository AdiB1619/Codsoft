package com.yourcompany.currencyconverter.controller;

import com.yourcompany.currencyconverter.model.dto.ConversionHistoryDto;
import com.yourcompany.currencyconverter.service.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final ConversionService conversionService;

    public HistoryController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Retrieves all conversion history records, ordered newest first.
     * @return List of {@link ConversionHistoryDto}
     */
    @GetMapping
    public ResponseEntity<List<ConversionHistoryDto>> getHistory() {
        return ResponseEntity.ok(conversionService.getHistory());
    }

    /**
     * Deletes a specific history record by its ID.
     * @param id The ID of the history record to delete
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        conversionService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
