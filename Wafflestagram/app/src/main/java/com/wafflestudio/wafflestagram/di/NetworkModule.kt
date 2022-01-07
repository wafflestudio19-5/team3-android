package com.wafflestudio.wafflestagram.di

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.wafflestagram.BuildConfig
import com.wafflestudio.wafflestagram.network.FeedService
import com.wafflestudio.wafflestagram.network.LoginService
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.SignUpService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        val builder = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
        )

        val token = sharedPreferences.getString("token", "")
        if(token != "") {
            builder.addInterceptor { chain ->
                val newRequest = chain.request().newBuilder().addHeader(
                    "Authentication", "$token"
                ).build()
                chain.proceed(newRequest)
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object PreferenceModule {
        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService {
        return retrofit.create(FeedService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}