package com.example.cryptoapi.config

import com.example.cryptoapi.websocket.PortfolioWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
@EnableWebSocket
class WebSocketConfig(private val portfolioHandler: PortfolioWebSocketHandler) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(portfolioHandler, "/ws/portfolio")
            .setAllowedOriginPatterns("*")
    }
}
