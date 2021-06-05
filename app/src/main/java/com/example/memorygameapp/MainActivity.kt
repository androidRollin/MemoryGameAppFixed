package com.example.memorygameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygameapp.models.BoardSize
import com.example.memorygameapp.models.utils.DEFAULT_ICONS

//Android Studio
//Vector Icons
//Margin, Padding, Content
//RecyclerView, Adapter


//Kotlin .kt
//Activity, one screen on the application
//Core logic
//logic view
//activity_main
//R.layout/.activity_main

class MainActivity : AppCompatActivity() {

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize: BoardSize = BoardSize.HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        //Dynamically set the width and height
        //In the recycler view
        //Every recycler view
        //adapter
        //recycler managger

        //get the default
        //randomized
        //take a certain numbers
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()


        rvBoard.adapter = MemoryBoardAdapter(this, boardSize,randomizedImages)

        //size is always defined, how many memory cards
        //application that is mroe efficient
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }
}