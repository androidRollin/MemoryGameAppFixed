package com.example.memorygameapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygameapp.models.BoardSize
import com.example.memorygameapp.models.utils.EXTRA_BOARD_SIZE
import com.example.memorygameapp.models.utils.EXTRA_GAME_NAME
import com.example.memorygameapp.models.utils.isPermissionGranted
import com.example.memorygameapp.models.utils.requestPermission
import com.example.memorygameapp.utils.BitmapScaler
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class CreateActivity : AppCompatActivity() {

    //I made some changes
    //I made some changes
    //I know now whats happening
    //Git changes
    //Lala
    companion object {
        private const val TAG = "CreateActivity"
        private const val PICK_PHOTO_CODE = 655
        private const val READ_EXTERNAL_PHOTOS_CODE = 248
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val MIN_GAME_LENGTH = 3
        private const val MAX_GAME_LENGTH = 14
    }

    //1) Define the lateinit vars
    private lateinit var rvImagePicker: RecyclerView
    private lateinit var etGameName: EditText
    private lateinit var btnSave: Button
    private lateinit var pbUploading: ProgressBar

    private lateinit var adapter: ImagePickerAdapter
    private lateinit var boardSize: BoardSize
    private var numImagesRequired = -1
    //Directory path
    private var chosenImageUris = mutableListOf<Uri>()
    private val storage = Firebase.storage
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        //2 Define the findViewById's
        rvImagePicker = findViewById(R.id.rvImagePicker)
        etGameName = findViewById(R.id.etGameName)
        btnSave = findViewById(R.id.btnSave)
        pbUploading = findViewById(R.id.pbUploading)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pics (0 / $numImagesRequired)"

        //Maximum length, always validate the user input
        //Help prevent unexpected happening

        btnSave.setOnClickListener {
            saveDataToFirebase()
        }

        etGameName.filters = arrayOf(InputFilter.LengthFilter(MAX_GAME_LENGTH))
        etGameName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                btnSave.isEnabled = shouldEnableSavedButton()
            }

        })

        adapter = ImagePickerAdapter(this, chosenImageUris, boardSize, object: ImagePickerAdapter.ImageClickListener{
            override fun onPlaceholderClicked() {
                //if isPermissionGranted
                if (isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION)){
                    launchIntentForPhotos() //implicit intent -> choose out of gogole
                } else { //request permission
                    requestPermission(this@CreateActivity, READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTOS_CODE) //request permission, get a call back if the user accepted
                }

            }

        } )

        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_PHOTOS_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchIntentForPhotos()
            } else {
                Toast.makeText(this, "In order to create a custom game, you need to provide access to your photos", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if ( item.itemId  ==  android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode != PICK_PHOTO_CODE || resultCode != Activity.RESULT_OK || data == null){
            Log.w(TAG, "Did not get data back from the launched activity, user likely cancelled the flow")
            return
        }
        //only support one photo, the data will come back
        val selectedUri = data.data
        val clipData = data.clipData
        if(clipData != null){
            Log.i(TAG, "clipData numImages ${clipData.itemCount} $clipData")
            for (i in 0 until clipData.itemCount) {
                val clipItem = clipData.getItemAt(i)
                if (chosenImageUris.size < numImagesRequired){
                    chosenImageUris.add(clipItem.uri)
                }
            }
        } else if (selectedUri != null){
            Log.i(TAG, "data: $selectedUri")
            chosenImageUris.add(selectedUri)
        }
        adapter.notifyDataSetChanged()
        supportActionBar?.title = "Choose pics (${chosenImageUris.size} / $numImagesRequired)"

        btnSave.isEnabled = shouldEnableSavedButton()

    }

    private fun saveDataToFirebase() {
        Log.i(TAG, "saveDataToFirebase")
        btnSave.isEnabled = false
        //Core logic
        //Use of internet
        val customGameName = etGameName.text.toString()
        // Check that we're not over writing someone else's data
        db.collection("games").document(customGameName).get().addOnSuccessListener { document ->
            if (document != null && document.data != null){
                AlertDialog.Builder(this)
                    .setTitle("Name taken")
                    .setMessage("A game already exists with the name '$customGameName', Please choose another" )
                    .setPositiveButton("OK", null)
                    .show()
                btnSave.isEnabled = true
            } else {
                handleImageUploading(customGameName)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Encountered error while saving memory game", exception)
            Toast.makeText(this, "Encountered error while saving memory game", Toast.LENGTH_SHORT).show()
            btnSave.isEnabled = true
        }

    }

    private fun handleImageUploading(gameName: String) {
        pbUploading.visibility = View.VISIBLE
        var didEncounterError = false
        val uploadedImageUrls = mutableListOf<String>()
        for ((index, photoUri) in chosenImageUris.withIndex()){
            val imageByteArray = getImageByteArray(photoUri)
            //4 images in the corgi game
            //this will be saved in the
            val filePath = "images/$gameName/${System.currentTimeMillis()}-${index}.jpg"
            val photoReference = storage.reference.child(filePath)
            photoReference.putBytes(imageByteArray)
                .continueWithTask {  photoUploadTask ->
                    Log.i(TAG, "Uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                    photoReference.downloadUrl
                }.addOnCompleteListener { downloadUrlTask ->
                    if (!downloadUrlTask.isSuccessful){
                        Log.e(TAG, "Exception with Firebase storage", downloadUrlTask.exception)
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        didEncounterError = true
                        return@addOnCompleteListener
                    }
                    if (didEncounterError) {
                        pbUploading.visibility = View.GONE
                        return@addOnCompleteListener
                    }
                    val downloadUrl = downloadUrlTask.result.toString()
                    uploadedImageUrls.add(downloadUrl)
                    pbUploading.progress = uploadedImageUrls.size * 100 / chosenImageUris.size
                    Log.i(TAG, "Finished uploading $photoUri, num uploaded ${uploadedImageUrls.size}")
                    if (uploadedImageUrls.size == chosenImageUris.size){
                        handleAllImagesUploaded(gameName, uploadedImageUrls)
                    }
                }
        }
    }

    private fun handleAllImagesUploaded(gameName: String, imageUrls: MutableList<String>) {
        db.collection("games").document(gameName)
            .set(mapOf("images" to imageUrls))
            .addOnCompleteListener { gameCreationTask ->
                pbUploading.visibility = View.GONE
                if(!gameCreationTask.isSuccessful){
                    Log.e(TAG, "Exception with Game Creation", gameCreationTask.exception)
                    Toast.makeText(this, "Failed game creation", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                Log.i(TAG, "Successfully created game $gameName")
                AlertDialog.Builder(this)
                    .setTitle("Upload Complete! Let's play your game $gameName")
                    .setPositiveButton("OK") { _, _ ->
                        val resultData = Intent()
                        resultData.putExtra(EXTRA_GAME_NAME, gameName)
                        setResult(Activity.RESULT_OK, resultData)
                        finish()
                    }.show()
            }

    }

    private fun getImageByteArray(photoUri: Uri): ByteArray {
        val originalBitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }
        Log.i(TAG, "Original width ${originalBitmap.width} and height ${originalBitmap.height}")
        val scaledBitmap = BitmapScaler.scaleToFitHeight(originalBitmap, 250)
        Log.i(TAG, "Scaled width ${originalBitmap.width} and height ${originalBitmap.height}")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
        return byteOutputStream.toByteArray()
    }

    //Creation flow, permissions, intent, and filling out images
    private fun shouldEnableSavedButton(): Boolean {
        //Check if we should enable the save button or not
        if (chosenImageUris.size != numImagesRequired)
        {
            return false
        }
        if (etGameName.text.isBlank() || etGameName.text.length < MIN_GAME_LENGTH )
        {
            return false
        }
        return true
    }


    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) //chose photos and import in application, // FILES ON android phone
        startActivityForResult(Intent.createChooser(intent, "Choose pics"), PICK_PHOTO_CODE) //Basically that have multiple apps, a request code
    }


}