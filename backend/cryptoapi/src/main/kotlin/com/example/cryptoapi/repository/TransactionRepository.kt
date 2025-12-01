package com.example.cryptoapi.repository

import com.example.cryptoapi.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Transaction>
}
