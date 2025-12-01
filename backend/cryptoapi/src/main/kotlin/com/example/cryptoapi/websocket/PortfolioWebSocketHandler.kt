package com.example.cryptoapi.websocket

import com.example.cryptoapi.service.PortfolioService
import com.example.cryptoapi.repository.UserRepository
import com.example.cryptoapi.util.JwtUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArraySet


@Component
class PortfolioWebSocketHandler(
    private val portfolioService: PortfolioService,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArraySet<WebSocketSession>()
    private val mapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // robust query parsing
        val rawQuery = session.uri?.query ?: ""
        val params = rawQuery.split("&").mapNotNull {
            val parts = it.split("=")
            if (parts.size >= 2) parts[0] to parts.subList(1, parts.size).joinToString("=") else null
        }.toMap()

        val rawToken = params["token"]?.let { java.net.URLDecoder.decode(it, "UTF-8") }
        println("WS token raw: $rawToken")

        if (rawToken == null) {
            println("No token provided in ws connection, closing")
            session.close()
            return
        }

        if (!jwtUtil.validateToken(rawToken)) {
            println("Token invalid/expired, closing WS")
            session.close()
            return
        }

        // extract username (subject) from token — works with your current JWT payload
        val username = jwtUtil.extractUsername(rawToken)
        println("Token subject (username): $username")

        val user = username?.let { userRepository.findByUsername(it) }

        if (user == null) {
            println("No user found for username=$username; closing WS")
            session.close()
            return
        }

        val userId = user.id
        session.attributes["user_id"] = userId
        sessions.add(session)
        println("WebSocket connected for userId=$userId (username=$username)")

        // ✅ Send initial portfolio immediately
        val portfolio = portfolioService.getUserPortfolio(userId)
        val json = mapper.writeValueAsString(portfolio)
        session.sendMessage(TextMessage(json))
    }




    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        sessions.remove(session)
    }

    // Broadcast every 10s
    @Scheduled(fixedRate = 5000)
    fun broadcastPortfolioUpdates() {
        sessions.forEach { session ->
            try {
                val userId = session.attributes["user_id"] as? Long
                if (userId != null) {
                    val portfolio = portfolioService.getUserPortfolio(userId)
                    println("Broadcasting portfolio for userId=$userId, size=${portfolio.size}")
                    val json = mapper.writeValueAsString(portfolio)
                    session.sendMessage(TextMessage(json))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
