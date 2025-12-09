package com.example.trevia.data.leancloud

import cn.leancloud.LCUser
import com.example.trevia.domain.login.UserModel
import javax.inject.Inject

class LeanCloudRepository @Inject constructor(private val leanCloudService: LeanCloudService)
{
    suspend fun register(username: String, password: String, email: String): UserModel
    {
        return leanCloudService.register(username, password, email).toModel()
    }

    private fun LCUser.toModel(): UserModel
    {
        return UserModel(
            this.objectId,
            this.username,
            this.email
        )
    }
}