package com.example.cryptoapi.dto


data class CoinCapPriceResponse(
    val data: Map<String, CoinCapPriceAsset>,
    val timestamp: Long
)