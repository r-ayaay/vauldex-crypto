package com.example.cryptoapi.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AddHoldingRequest(
    @field:NotBlank(message = "Symbol must not be empty")
    val symbol: String,

    @field:Min(value = 0, message = "Amount must be greater than zero")
    val amount: Double
)