package com.yourcompany.currencyconverter.repository;

import com.yourcompany.currencyconverter.model.entity.ConversionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {
    
    /**
     * Retrieve all conversion history records ordered by the most recent first.
     */
    List<ConversionHistory> findAllByOrderByConvertedAtDesc();
}
