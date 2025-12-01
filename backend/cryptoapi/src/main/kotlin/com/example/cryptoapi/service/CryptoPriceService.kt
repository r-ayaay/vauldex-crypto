package com.example.cryptoapi.service

import com.example.cryptoapi.entity.CryptoPrice
import com.example.cryptoapi.repository.CryptoPriceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CryptoPriceService(
    private val cryptoPriceRepository: CryptoPriceRepository
) {

    fun getAllPrices(): List<CryptoPrice> = cryptoPriceRepository.findAll()

    fun getPriceBySymbol(symbol: String): CryptoPrice? = cryptoPriceRepository.findBySymbol(symbol)

    @Transactional
    fun saveOrUpdate(symbol: String, name: String, priceUsd: Double) {
        val existing = cryptoPriceRepository.findBySymbol(symbol)
        if (existing != null) {
            cryptoPriceRepository.save(
                existing.copy(
                    priceUsd = priceUsd,
                    lastUpdated = LocalDateTime.now()
                )
            )
        } else {
            cryptoPriceRepository.save(
                CryptoPrice(
                    symbol = symbol,
                    name = name,
                    priceUsd = priceUsd
                )
            )
        }
    }
}
