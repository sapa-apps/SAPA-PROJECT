package com.sapa.signlanguage.data.pref

data class UserModel(
    val email: String = "",
    val token: String = "",
    val isLogin: Boolean = false,
    val isGuest: Boolean = false  // Menandakan apakah user adalah guest
)
