package com.bryll.hams.config




import com.bryll.hams.utils.DataStorage
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bryll.hams.BuildConfig
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.auth.AuthRepositoryImpl
import com.bryll.hams.repository.datastore.DataStoreImpl
import com.bryll.hams.repository.enrollment.EnrollmentRepository
import com.bryll.hams.repository.enrollment.EnrollmentRepositoryImpl
import com.bryll.hams.service.AuthService
import com.bryll.hams.service.EnrollmentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModules {
    private const val BASE_URL = "http://${BuildConfig.API}/"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
    private val builder = OkHttpClient.Builder().also {  client ->
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            client.addInterceptor(logging)

        }

    }.build()

    private val api: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(@ApplicationContext context: Context) : AuthRepository {
        return  AuthRepositoryImpl(api.create(AuthService::class.java))
    }

    @Singleton
    @Provides
    fun provideDataStorage(@ApplicationContext context: Context) : com.bryll.hams.repository.datastore.DataStore {
        return DataStoreImpl(context.dataStore)
    }

    @Singleton
    @Provides
    fun provideEnrollmentRepository() : EnrollmentRepository {
        return  EnrollmentRepositoryImpl(api.create(EnrollmentService::class.java))
    }
 }