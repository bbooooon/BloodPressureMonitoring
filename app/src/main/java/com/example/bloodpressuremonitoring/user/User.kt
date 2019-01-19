package com.example.bloodpressuremonitoring.user

object User {
    private val user: UserProfile = UserProfile()
    public var access: Boolean = false

    fun getUser(): UserProfile {
        return user
    }
}