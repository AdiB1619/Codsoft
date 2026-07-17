package com.yourcompany.currencyconverter.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FavoriteRequest {

    @NotBlank(message = "Currency code must not be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be exactly 3 uppercase letters")
    private String currency;

    public FavoriteRequest() {}

    public FavoriteRequest(String currency) {
        this.currency = currency;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
