package com.example.aptikma_remake.data.DI

import android.content.Context
import androidx.room.Room
import com.example.aptikma_remake.data.network.*
import com.example.aptikma_remake.util.Constans
import com.example.aptikma_remake.util.Constans.BASE_URL
//import com.example.kud.data.db.MyDatabase
//import com.example.kud.data.network.AuthInterceptor
//import com.example.kud.data.network.UserApi
//import com.example.kud.utils.Constans
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//    @Singleton
//    @Provides
//    fun providesRetrofit(): Builder {
//        return Builder()
//            .baseUrl(Constans.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//    }
//
//    @Singleton
//    @Provides
//    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .connectTimeout(20, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }

    @Singleton
    @Provides
    fun providesRetrofit(): Builder {
        return Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Builder): AuthApi {
        return retrofitBuilder.build()
            .create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun home(retrofitBuilder: Builder, okHttpClient: OkHttpClient): HomeApi {
        return retrofitBuilder.client(okHttpClient).build().create(HomeApi::class.java)
    }

    @Singleton
    @Provides
    fun profile(retrofitBuilder: Builder, okHttpClient: OkHttpClient): ProfileApi {
        return retrofitBuilder.client(okHttpClient).build().create(ProfileApi::class.java)
    }

    @Singleton
    @Provides
    fun attendance(retrofitBuilder: Builder, okHttpClient: OkHttpClient): AttendanceApi {
        return retrofitBuilder.client(okHttpClient).build().create(AttendanceApi::class.java)
    }

    @Singleton
    @Provides
    fun sallary(retrofitBuilder: Builder, okHttpClient: OkHttpClient): SallaryApi {
        return retrofitBuilder.client(okHttpClient).build().create(SallaryApi::class.java)
    }

//    @Singleton
//    @Provides
//    fun provideDatabase(
//        @ApplicationContext context: Context
//    ) = Room.databaseBuilder(
//        context,
//        MyDatabase::class.java,
//        "person_database"
//    ).build()
//
//    @Singleton
//    @Provides
//    fun provideDao(database: MyDatabase) = database.myDao()

}