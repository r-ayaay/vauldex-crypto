package com.example.cryptoapi.service

import com.example.cryptoapi.entity.Portfolio
import com.example.cryptoapi.entity.User
import com.example.cryptoapi.repository.CryptoPriceRepository
import com.example.cryptoapi.repository.PortfolioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository,
    private val cryptoPriceService: CryptoPriceService,
    private val cryptoPriceRepository: CryptoPriceRepository   // ⬅ Add this
) {

    fun getUserPortfolio(userId: Long): List<PortfolioWithValue> {
        val holdings = portfolioRepository.findAllByUserId(userId)
        return holdings.map { portfolio ->
            val currentPrice = cryptoPriceService.getPriceBySymbol(portfolio.symbol)?.priceUsd ?: 0.0
            PortfolioWithValue(
                id = portfolio.id,
                symbol = portfolio.symbol,
                amount = portfolio.amount,
                currentPrice = currentPrice,
                totalValue = portfolio.amount * currentPrice
            )
        }
    }

    @Transactional
    fun addHolding(user: User, symbol: String, amount: Double): Portfolio {
        val normalized = symbol.uppercase()

        // 🔍 Validate that the symbol exists in the crypto price table
        val exists = cryptoPriceRepository.findBySymbol(normalized)
            ?: throw IllegalArgumentException("Symbol '$normalized' does not exist in price table.")

        val portfolio = Portfolio(
            user = user,
            symbol = normalized,
            amount = amount
        )
        return portfolioRepository.save(portfolio)
    }

    @Transactional
    fun updateHolding(portfolioId: Long, amount: Double): Portfolio {
        val portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow { IllegalArgumentException("Portfolio not found") }

        val updated = portfolio.copy(amount = amount)
        return portfolioRepository.save(updated)
    }

    @Transactional
    fun deleteHolding(portfolioId: Long) {
        portfolioRepository.deleteById(portfolioId)
    }

    data class PortfolioWithValue(
        val id: Long,
        val symbol: String,
        val amount: Double,
        val currentPrice: Double,
        val totalValue: Double
    )

    data class PortfolioSummary(
        val holdings: List<PortfolioWithValue>,
        val totalPortfolioValue: Double
    )

    fun getUserPortfolioSummary(userId: Long): PortfolioSummary {
        val holdings = getUserPortfolio(userId)
        if (holdings.isEmpty()) {
            return PortfolioSummary(emptyList(), 0.0)
        }
        val totalValue = holdings.sumOf { it.totalValue }
        return PortfolioSummary(
            holdings = holdings,
            totalPortfolioValue = totalValue
        )
    }
}

