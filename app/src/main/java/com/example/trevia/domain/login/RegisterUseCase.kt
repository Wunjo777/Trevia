package com.example.trevia.domain.login

import com.example.trevia.data.leancloud.LeanCloudRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val leanCloudRepository: LeanCloudRepository)
{
    suspend operator fun invoke(username: String, password: String, email: String)
    {
        leanCloudRepository.register(username, password, email)
    }
}