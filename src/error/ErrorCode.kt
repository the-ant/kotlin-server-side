package com.theant.error


object ErrorCode {
    // Auth
    const val USER_ALREADY_EXISTS = 1001
    const val MISSING_USERNAME = 1002
    const val MISSING_PASSWORD = 1003
    const val MISSING_DISPLAY_NAME = 1004
    const val USER_TOKEN_EXPIRED = 1005

    // Todos
    const val MISSING_TODO = 2001
    const val TODO_NOT_EXIST = 2001
}