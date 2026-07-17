package com.yourcompany.currencyconverter.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for ConversionHistory.
 * Flattens the Currency entities to just their ISO codes to simplify JSON output.
 */
public class ConversionHistoryDto {

    private Long id;
    private String from;
    private String to;
    private BigDecimal amount;
    private BigDecimal result;
    private BigDecimal rate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ConversionHistoryDto() {}

    public ConversionHistoryDto(Long id, String from, String to, BigDecimal amount, BigDecimal result, BigDecimal rate, LocalDateTime timestamp) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.result = result;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getResult() { return result; }
    public void setResult(BigDecimal result) { this.result = result; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
