package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.search.data.network.ItunesApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ItunesApiService> {
        get<Retrofit>().create(ItunesApiService::class.java)
    }
}