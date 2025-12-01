package com.example.cryptoapi.entity

import jakarta.persistence.*

@Entity
@Table(name = "portfolios")
data class Portfolio(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val symbol: String, // BTC, ETH, etc.

    @Column(nullable = false)
    val amount: Double // how much of this crypto the user owns
)
