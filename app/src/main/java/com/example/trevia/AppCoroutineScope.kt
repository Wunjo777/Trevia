package com.example.trevia

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCoroutineScope @Inject constructor() {
    val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
}
