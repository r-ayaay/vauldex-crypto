package com.example.cryptoapi.dto

data class PortfolioResponse(
    val id: Long,
    val symbol: String,
    val amount: Double,
)
