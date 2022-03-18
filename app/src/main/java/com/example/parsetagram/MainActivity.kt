package com.example.parsetagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

/**
 * Let user create a post by taking a photo with their camera
 * */

class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setting the description of the post
        // 2. A button to launch the camera to take a picture
        // 3. An imageView to show the picture the user has shown
        // 4. A button to save and send the post to a parse server

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // send post to server without an image
            // Get the description they have inputted
            val description = findViewById<EditText>(R.id.et_description).text.toString()
            val user = ParseUser.getCurrentUser()

            if(photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(TAG, "Error while picking image")
                Toast.makeText(this, "Image was not picked", Toast.LENGTH_SHORT)
            }
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            ParseUser.logOut()
            val currentUser = ParseUser.getCurrentUser() // this will now be null
            goToLoginActivity()
        }

        findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to take a picture
            onLaunchCamera()
        }

        // setOnItemSelectedListener will let you know which specific item was clicked
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {

            // Arrow symbol passes in variable or else we can use generic variable 'it' which is passed by default (for example it.itemId)
                item ->
            when (item.itemId) {

                R.id.action_home -> {
                    // TODO Navigate to home screen
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()

                }
                R.id.action_compose -> {
                    // TODO navigate to compose screen
                    Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show()

                }
                R.id.action_profile -> {
                    // TODO navigate to profile screen
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()


                }
            }

            // Return true to say that we've handled user intreaction on the item
            true
        }

        // queryPosts()
    }

    // Send a post object to our parse server
    fun submitPost(description: String, user: ParseUser, file: File  ) {
        // Create a post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground{ exception ->
            if (exception != null) {
                // Something has went wrong
                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()
                Toast.makeText(this, "Error submitting post", Toast.LENGTH_SHORT)
            } else {
                Log.i(TAG, "Successfully saved post")
                findViewById<EditText>(R.id.et_description).text.clear()  // Resetting the edittext field to empty
                findViewById<ImageView>(R.id.imageView).setImageDrawable(null)   // Reset the image to be empty
                Toast.makeText(this, "Post Successful!", Toast.LENGTH_SHORT)
            }
        }
    }


    // Called when user comes back from camera app, we check the request code to see its the same request
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to launch camera app, uses intent to do so
    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    // Query for all post in our server
    fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        // Find all the Post objects
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has gone wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if(posts != null) {
                        for(post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + ", username: " +
                            post.getUser()?.username) // print out description
                        }
                    }
                }
            }

        })
    }

    // After login send to main activity
    private fun goToLoginActivity() {
        // navigates to main activity
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        // closes login activity
        finish()
    }

    companion object {
        const val  TAG = "MainActivity"
    }

}
