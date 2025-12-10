package com.example.trevia.domain.login.usecase

import com.example.trevia.data.leancloud.UserRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val repo: UserRepository)
{
    suspend operator fun invoke()
    {
        repo.logOut()
    }
}