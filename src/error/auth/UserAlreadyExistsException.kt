package com.theant.error.auth

import com.theant.model.User

class UserAlreadyExistsException(user: User): Exception("$user already exists!") {
}
