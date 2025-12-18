package com.example.trevia.utils

class LeanCloudFailureException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

