package com.example.memorygameapp

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygameapp.models.BoardSize
import com.example.memorygameapp.models.MemoryCard
import com.example.memorygameapp.models.MemoryGame
import com.example.memorygameapp.models.utils.DEFAULT_ICONS
import com.google.android.material.snackbar.Snackbar

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

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))

        //Dynamically set the width and height
        //In the recycler view
        //Every recycler view
        //adapter
        //recycler managger

        //get the default
        //randomized
        //take a certain numbers

        //one memory card

        memoryGame = MemoryGame(boardSize)
         adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        //size is always defined, how many memory cards
        //application that is more efficient
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }

    //The idea, this method, updating the attempted flip
    private fun updateGameWithFlip(position: Int) {
        //Error Checking
        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)){
            //alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid Move", Snackbar.LENGTH_SHORT).show()
            return
        }
        //Updating the memory game, with the attempted flip
        //Handling what happens in the state of the game

        //Actually flip over the card
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Found a match! Num pairs found: ${memoryGame.numPairsFound}")
            //add in, color interpolation in the number of pairs
            //increment the number of pairs, update the color

            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full),
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(clRoot, "You won! Congratulations", Snackbar.LENGTH_LONG).show()
            }

        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}