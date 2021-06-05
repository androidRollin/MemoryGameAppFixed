package com.example.memorygameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygameapp.models.BoardSize
import com.example.memorygameapp.models.MemoryCard
import com.example.memorygameapp.models.MemoryGame
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

//Create classes to manage memory game
//will have an associated state
//face-up or face-down

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG ="MainActivity"
    }

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize: BoardSize = BoardSize.EASY

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

        //one memory card

        val memoryGame = MemoryGame(boardSize)
        rvBoard.adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                Log.i(TAG, "Card clicked $position")
            }

        })

        //size is always defined, how many memory cards
        //application that is more efficient
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }
}