package com.example.cryptoapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class CryptoPriceUpdater(
    private val cryptoPriceService: CryptoPriceService,
    @Value("\${coincap.api-key}") private val apiKey: String
) {

    private val restTemplate = RestTemplate()

    // List of symbols you want to track
    private val cryptoSymbols = listOf("btc","eth","shib","doge", "ada", "sol", "pepe", "xrp")

    // Map symbol to display name
    private val cryptoNames = mapOf(
        "btc" to "Bitcoin",
        "eth" to "Ethereum",
        "shib" to "Shiba Inu",
        "doge" to "Dogecoin",
        "ada" to "Cardano",
        "sol" to "Solana",
        "pepe" to "Pepe Coin",
        "xrp" to "Ripple"
    )

    // DTO for deserialization
    data class CoinCapPriceResponse(
        val timestamp: Long,
        val data: List<String>
    )

    @Scheduled(fixedRate = 10_000) // update every 10 seconds
    fun updatePrices() {
        try {
            val symbolsParam = cryptoSymbols.joinToString(",") { it.lowercase() }
            val url = "https://rest.coincap.io/v3/price/bysymbol/$symbolsParam"

            // Bearer auth header
            val headers = HttpHeaders()
            headers.setBearerAuth(apiKey)
            val entity = HttpEntity<String>(headers)

            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CoinCapPriceResponse::class.java
            )

            val prices = response.body?.data
            if (prices != null && prices.size == cryptoSymbols.size) {
                cryptoSymbols.forEachIndexed { index, symbol ->
                    cryptoPriceService.saveOrUpdate(
                        symbol = symbol.uppercase(),
                        name = cryptoNames[symbol] ?: symbol.uppercase(),
                        priceUsd = prices[index].toDouble()
                    )
                }
                println("Updated crypto prices at ${response.body?.timestamp}")
            } else {
                println("Mismatch in prices size or empty response")
            }
        } catch (e: Exception) {
            println("Failed to update crypto prices: ${e.message}")
        }
    }
}
