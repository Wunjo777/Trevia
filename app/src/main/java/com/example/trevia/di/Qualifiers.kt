package com.example.trevia.di

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineRepo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnlineRepo