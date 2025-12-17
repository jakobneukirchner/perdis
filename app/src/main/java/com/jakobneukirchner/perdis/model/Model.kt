package com.jakobneukirchner.perdis.model

data class Credentials(
    val username: String,
    val password: String
)

data class Fahrt(
    val id: String,
    val abfahrtszeit: String,
    val ankunftszeit: String,
    val linie: String,
    val kurs: String = "",
    val von: String,
    val nach: String,
    val ort: String
)

data class Dienst(
    val id: String,
    val datum: String,
    val bezeichnung: String,
    val fahrten: List<Fahrt>
)

data class LoginState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class DienstplanState(
    val isLoading: Boolean = false,
    val dienste: List<Dienst> = emptyList(),
    val errorMessage: String? = null
)

data class TagesplanState(
    val datum: String = "",
    val dienst: Dienst? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
