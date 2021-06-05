package com.example.memorygameapp.models

//List out the attribute of the memory card
data class MemoryCard(
    val identity: Int, //layout resource id
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)