package com.example.aptikma_remake.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.aptikma_remake.data.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                if (response.isSuccessful) {
                    NetworkResult.Success(data = response.body()!!)
                } else {
                    val errorResponse =
                        JSONObject(response.errorBody()!!.charStream().readText())
                    NetworkResult.Error(errorResponse.toString() + "Wrong Email or Password")
                }
            } catch (e: HttpException) {
                NetworkResult.Error(e.message ?: "Something went wrong 1")
            } catch (e: IOException) {
//                No Connection or URL error or bad connection
                NetworkResult.Error(e.toString())
//                NetworkResult.Error("Request Time Out")
            } catch (e: Exception) {
//                NetworkResult.Error(e.toString() + "Url not found error code 404" )
                NetworkResult.Error("Url not found error code 404")
            } catch (e: Throwable) {
                NetworkResult.Error(e.toString() + "Something went wrong 3")
            } catch (e: SocketTimeoutException) {
                NetworkResult.Error(e.message + "Socket time out exception")
            }
        }
    }
}