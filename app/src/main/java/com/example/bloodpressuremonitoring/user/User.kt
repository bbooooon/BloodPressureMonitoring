package com.example.bloodpressuremonitoring.user

import com.example.bloodpressuremonitoring.classify.UserData

object User {
    private val user: UserProfile = UserProfile()
    public var access: Boolean = false

    public var userinfo: AddUser = AddUser()
    fun getUser(): UserProfile {
        return user
    }
}