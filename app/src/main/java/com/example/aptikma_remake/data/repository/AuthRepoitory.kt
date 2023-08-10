package com.example.aptikma_remake.data.repository

import com.example.aptikma_remake.data.model.LoginRequest
import com.example.aptikma_remake.data.network.AuthApi
import javax.inject.Inject

class AuthRepoitory @Inject constructor(private val api: AuthApi) : BaseRepository() {
    suspend fun loginUser(username: String, password: String, token: String) =
        safeApiCall { api.loginUser(username, password, token) }

}