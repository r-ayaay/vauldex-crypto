package com.example.cryptoapi.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "crypto_prices")
data class CryptoPrice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val symbol: String, // BTC, ETH, etc.

    @Column(nullable = false)
    val name: String, // Bitcoin, Ethereum, etc.

    @Column(nullable = false)
    val priceUsd: Double, // current price in USD

    @Column(nullable = false)
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)
