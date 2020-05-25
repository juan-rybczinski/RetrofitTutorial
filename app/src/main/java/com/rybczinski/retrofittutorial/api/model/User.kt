package com.rybczinski.retrofittutorial.api.model

class User(
    val name: String,
    val email: String,
    val age: Int,
    val topics: Array<String>
){
    var id: String? = null
}
