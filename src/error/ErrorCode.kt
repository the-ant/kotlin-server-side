package com.theant.error


object ErrorCode {
    // Auth
    const val USER_ALREADY_EXISTS = 1001
    const val MISSING_USERNAME = USER_ALREADY_EXISTS + 1
    const val MISSING_PASSWORD = MISSING_USERNAME + 1
    const val MISSING_DISPLAY_NAME = MISSING_PASSWORD + 1

}