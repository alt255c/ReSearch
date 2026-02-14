package tech.alt255.research.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.alt255.research.data.remote.AuthService
import tech.alt255.research.data.remote.NotificationService
import tech.alt255.research.data.remote.QuestService
import tech.alt255.research.data.remote.RatingService
import tech.alt255.research.data.remote.UserService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL_DEBUG = "https://alt255.tech/ReSearch/"
    private const val BASE_URL_RELEASE = "https://alt255.tech/ReSearch/"

    @Provides
    @Singleton
    fun provideBaseUrl(@ApplicationContext context: Context): String {
        val isDebug = context.applicationInfo != null &&
                (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        return if (isDebug) BASE_URL_DEBUG else BASE_URL_RELEASE
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestService(retrofit: Retrofit): QuestService {
        return retrofit.create(QuestService::class.java)
    }

    @Provides
    @Singleton
    fun provideRatingService(retrofit: Retrofit): RatingService {
        return retrofit.create(RatingService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService {
        Log.d("NetworkModule", "Creating NotificationService")
        return retrofit.create(NotificationService::class.java)
    }
}