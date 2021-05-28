package com.example.memorygameapp

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min


//private val to use this in the body of the class
//Sub class of the recyclerview adapter, parameterized by recycler view holdder, provides all the view in all recycler view

//private val to use in the class
//parametrized
//viewholder -> access to one recycler view element

//abstract class
//several methods for it to function
class MemoryBoardAdapter(private val context: Context, private val numPieces: Int) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    //singelton which we define constants
    //static class
    companion object{
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    //create one view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 2 - (2* MARGIN_SIZE) //margin size
        val cardHeight = parent.height / 4 - (2* MARGIN_SIZE) //margin size
        //each memory card is going to be square
        val cardSideLength = min(cardWidth,cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.CardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }


    //Taking the data and pinding it into viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        //bind is not yet defined
    }

    //how many elements in the recycler view
    override fun getItemCount() = numPieces



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //inside this, when we bind the view holder, reference to the image button inside cardview
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)


        fun bind(position: Int) {
            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on position $position")
            }
        }
    }
}
