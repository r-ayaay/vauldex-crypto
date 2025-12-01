package com.example.cryptoapi.controller

import com.example.cryptoapi.dto.AddHoldingRequest
import com.example.cryptoapi.dto.PortfolioResponse
import com.example.cryptoapi.dto.UpdateHoldingRequest
import com.example.cryptoapi.entity.Portfolio
import com.example.cryptoapi.entity.User
import com.example.cryptoapi.entity.Transaction
import com.example.cryptoapi.repository.PortfolioRepository
import com.example.cryptoapi.repository.TransactionRepository
import com.example.cryptoapi.repository.UserRepository
import com.example.cryptoapi.service.CryptoPriceService
import com.example.cryptoapi.service.PortfolioService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/portfolio")
class PortfolioController(
    private val portfolioService: PortfolioService,
    private val cryptoPriceService: CryptoPriceService,
    private val userRepository: UserRepository,
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository
) {

    /** ---------------------------------------------------------
     *  GET PORTFOLIO (ADMIN OR CLIENT USING ID)
     * --------------------------------------------------------- */
    @GetMapping("/{userId}")
    fun getPortfolio(@PathVariable userId: Long): List<PortfolioService.PortfolioWithValue> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        return portfolioService.getUserPortfolio(user.id)
    }

    /** ---------------------------------------------------------
     *  GET PORTFOLIO FOR CURRENT USER (JWT AUTH)
     * --------------------------------------------------------- */
    @GetMapping("/me")
    fun getPortfolioForCurrentUser(): List<PortfolioService.PortfolioWithValue> {
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw IllegalArgumentException("Not authenticated")

        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        return portfolioService.getUserPortfolio(user.id)
    }

    /** ---------------------------------------------------------
     *  ADD A HOLDING (MERGE IF EXISTS)
     * --------------------------------------------------------- */
    @PostMapping("/{userId}")
    @Transactional
    fun addHolding(
        @PathVariable userId: Long,
        @Valid @RequestBody request: AddHoldingRequest
    ): Portfolio {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val symbol = request.symbol.trim().uppercase()
        val amount = request.amount

        // Validate symbol exists in crypto price table
        val cryptoPrice = cryptoPriceService.getPriceBySymbol(symbol)
            ?: throw IllegalArgumentException("Invalid symbol: $symbol")

        // Check if user already owns this crypto
        val existingHolding = portfolioRepository.findByUserIdAndSymbol(user.id, symbol)

        val savedHolding = if (existingHolding != null) {
            val updated = existingHolding.copy(amount = existingHolding.amount + amount)
            portfolioRepository.save(updated)
        } else {
            portfolioRepository.save(
                Portfolio(
                    user = user,
                    symbol = symbol,
                    amount = amount
                )
            )
        }

        // Record the transaction
        transactionRepository.save(
            Transaction(
                user = user,
                symbol = symbol,
                amount = amount,
                type = "BUY",
                priceUsd = cryptoPrice.priceUsd
            )
        )

        return savedHolding
    }

    /** ---------------------------------------------------------
     *  UPDATE A HOLDING
     * --------------------------------------------------------- */
    @PutMapping("/{portfolioId}")
    fun updateHolding(
        @PathVariable portfolioId: Long,
        @Valid @RequestBody request: UpdateHoldingRequest
    ) {
        portfolioService.updateHolding(portfolioId, request.amount)
    }

    /** ---------------------------------------------------------
     *  DELETE A HOLDING
     * --------------------------------------------------------- */
    @DeleteMapping("/{portfolioId}")
    fun deleteHolding(@PathVariable portfolioId: Long) {
        portfolioService.deleteHolding(portfolioId)
    }

    /** ---------------------------------------------------------
     *  SUMMARY ENDPOINT
     * --------------------------------------------------------- */
    @GetMapping("/{userId}/summary")
    fun getPortfolioSummary(@PathVariable userId: Long): PortfolioService.PortfolioSummary {
        return portfolioService.getUserPortfolioSummary(userId)
    }

    /** ---------------------------------------------------------
     *  TRANSACTION HISTORY
     * --------------------------------------------------------- */
    @GetMapping("/{userId}/transactions")
    fun getTransactions(@PathVariable userId: Long): List<Transaction> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        return transactionRepository.findAllByUserIdOrderByCreatedAtDesc(user.id)
    }
}

