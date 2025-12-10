package com.example.trevia.domain.login.usecase

import android.util.Log
import com.example.trevia.data.leancloud.UserRepository
import com.example.trevia.domain.login.model.UserModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val repo: UserRepository)
{
    operator fun invoke(): StateFlow<UserModel?>
    {
        return repo.currentUser
    }
}