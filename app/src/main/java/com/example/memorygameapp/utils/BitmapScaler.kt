package com.example.memorygameapp.utils

import android.graphics.Bitmap

//nicer looking api
//harder to communicate
object BitmapScaler {
    //The intention, keep the aspect ratio, width and height

    //Scale and maintain aspect radio given a desired width
    //BitmapScaler.scaleToFitWidth(bitmap, 100);
    fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap {
        val factor = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
    }

    //Scale and maintain aspect ratio given a desired height
    fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap {
        val factor = height / b.height.toFloat()
        return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
    }
}
