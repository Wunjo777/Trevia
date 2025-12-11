package com.example.trevia.domain.login.usecase

import com.example.trevia.data.leancloud.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUseCase @Inject constructor(private val repo: UserRepository)
{
    suspend operator fun invoke(username: String, password: String, email: String)
    {
        repo.register(username, password, email)
    }
}