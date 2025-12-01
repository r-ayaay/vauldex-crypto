package com.example.cryptoapi.service

import com.example.cryptoapi.dto.RegisterRequest
import com.example.cryptoapi.dto.UserResponse
import com.example.cryptoapi.entity.User
import com.example.cryptoapi.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(@Valid @RequestBody request: RegisterRequest): UserResponse {
        if (userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already taken")
        }

        val hashedPassword = passwordEncoder.encode(request.password)!!

        val user = userRepository.save(
            User(
                username = request.username,
                password = hashedPassword
            )
        )

        return UserResponse(user.id, user.username)
    }
}
