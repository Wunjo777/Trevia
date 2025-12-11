package com.example.trevia.data.leancloud

import cn.leancloud.LCUser
import com.example.trevia.domain.login.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val leanCloudService: LeanCloudService)
{
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    suspend fun register(username: String, password: String, email: String): UserModel
    {
        return leanCloudService.register(username, password, email).toModel()
    }

    suspend fun login(username: String, password: String): UserModel
    {
        val user = leanCloudService.login(username, password).toModel()
        _currentUser.value = user
        return user
    }

    suspend fun logOut()
    {
        leanCloudService.logOut()
        _currentUser.value = null
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