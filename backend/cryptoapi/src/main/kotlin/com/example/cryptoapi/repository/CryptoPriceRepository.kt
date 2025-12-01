package com.example.cryptoapi.repository

import com.example.cryptoapi.entity.CryptoPrice
import org.springframework.data.jpa.repository.JpaRepository

interface CryptoPriceRepository : JpaRepository<CryptoPrice, Long> {
    fun findBySymbol(symbol: String): CryptoPrice?


}
