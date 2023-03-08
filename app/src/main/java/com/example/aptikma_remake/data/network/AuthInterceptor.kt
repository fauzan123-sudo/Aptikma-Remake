package com.example.aptikma_remake.data.network

import com.example.aptikma_remake.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: TokenManager

//    @Inject
//    lateinit var userPreferences: UserPreferences

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val token = tokenManager.getToken()
//        val tocen = userPreferences.accessToken
        request.addHeader("Authorization", "Bearer $token")
        request.addHeader("Accept", "application/json")
        return chain.proceed(request.build())
    }
}