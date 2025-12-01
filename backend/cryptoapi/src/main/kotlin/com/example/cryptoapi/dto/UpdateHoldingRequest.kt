package com.example.cryptoapi.dto

import jakarta.validation.constraints.Min

data class UpdateHoldingRequest(
    @field:Min(value = 0, message = "Amount must be greater than zero")
    val amount: Double
)