package com.example.trevia.domain.login.usecase

import com.example.trevia.data.remote.leancloud.UserRepository
import com.example.trevia.domain.login.model.UserModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentUserUseCase @Inject constructor(private val repo: UserRepository)
{
    operator fun invoke(): StateFlow<UserModel?>
    {
        return repo.currentUser
    }
}