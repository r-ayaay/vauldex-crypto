package com.example.cryptoapi.controller


import com.example.cryptoapi.dto.RegisterRequest
import com.example.cryptoapi.dto.UserResponse
import com.example.cryptoapi.entity.User
import com.example.cryptoapi.repository.UserRepository
import com.example.cryptoapi.service.UserService
import com.example.cryptoapi.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val userResponse = userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: RegisterRequest): ResponseEntity<Map<String, Any>> {
        val user = userRepository.findByUsername(request.username)
            ?: return ResponseEntity.status(401)
                .body(mapOf("error" to "Invalid credentials"))

        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.status(401)
                .body(mapOf("error" to "Invalid credentials"))
        }

        val token = jwtUtil.generateToken(user.username)
        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "username" to user.username,
                "user_id" to user.id
            )
        )
    }
}

