package com.example.cryptoapi.repository

import com.example.cryptoapi.entity.Portfolio
import com.example.cryptoapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByUserId(userId: Long): List<Portfolio>

    fun findByUserIdAndSymbol(userId: Long, symbol: String): Portfolio?

}
