package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCUser
import com.example.trevia.domain.login.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val leanCloudService: LeanCloudService)
{
    private val _currentUser =
        MutableStateFlow(leanCloudService.getCurrentUser()?.toModel())
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    suspend fun register(username: String, password: String, email: String): UserModel
    {
        return leanCloudService.register(username, password, email).toModel()
    }

    suspend fun login(username: String, password: String): UserModel
    {
        val user = leanCloudService.login(username, password).toModel()
        refreshCurrentUser()
        return user
    }

    suspend fun logOut()
    {
        leanCloudService.logOut()
        refreshCurrentUser()
    }

    fun refreshCurrentUser()
    {
        _currentUser.value = leanCloudService.getCurrentUser()?.toModel()
    }

    private fun LCUser.toModel(): UserModel
    {
        return UserModel(
            this.username,
            this.email
        )
    }
}