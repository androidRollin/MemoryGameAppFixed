package com.example.memorygameapp.models

import com.example.memorygameapp.models.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {


    val cards: List<MemoryCard>
    val numPairsFound = 0

    init{
        //initialization of the cards
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }
}