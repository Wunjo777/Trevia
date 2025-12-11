package com.example.trevia.domain.login.usecase

import com.example.trevia.data.leancloud.UserRepository
import com.example.trevia.domain.login.model.UserModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(private val repo: UserRepository)
{
    suspend operator fun invoke(username: String, password: String): UserModel
    {
        return repo.login(username, password)
    }
}