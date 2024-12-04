package com.sapa.signlanguage.api

data class RegisterRequest(
	val nama: String,
	val email: String,
	val password: String
)

data class LoginRequest(
	val token: String
)

data class ProfileResponse(
	val nama: String,
	val email: String,
	val fotoProfil: String?
)

data class UpdateProfileRequest(
	val nama: String?,
	val emailBaru: String?,
	val fotoProfil: String?,
	val passwordBaru: String?
)


