package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.search.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        AppDatabase.getInstance(androidContext())
    }
}