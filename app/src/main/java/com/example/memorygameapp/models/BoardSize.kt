package com.example.memorygameapp.models

//1 Passing 2 & 4 as dimension of the board
//column
//new package called model
//Kotlin File, called board size

enum class BoardSize(val numCards: Int){
    EASY(8),
    MEDIUM(18),
    HARD(24);

    //getWidth -> depending on the value
    //know what is the width of the game

    //this -> board size
    fun getWidth(): Int {
        //Switch statement
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    //fully determined by the number of cards
    fun getHeight(): Int {
        return numCards/ getWidth()
    }

    fun getNumPairs(): Int {
        return numCards/ 2
    }
}