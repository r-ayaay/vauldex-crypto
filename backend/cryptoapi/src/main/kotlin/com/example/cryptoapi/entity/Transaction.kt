package com.example.cryptoapi.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val symbol: String,

    @Column(nullable = false)
    val amount: Double, // positive for buy/add, negative for sell

    @Column(nullable = false)
    val type: String, // e.g., "BUY", "SELL", "UPDATE"

    @Column(nullable = false)
    val priceUsd: Double, // price at the time of transaction

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
