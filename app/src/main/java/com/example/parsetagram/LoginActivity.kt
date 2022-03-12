package com.example.parsetagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // check if user is logged in
        // if they are take them to main activity (this way they wont be taken to login activity)
        if(ParseUser.getCurrentUser() != null) {
            goToMainActivity() // if not null nav to mainActivity
        }

        // get input from user and prepare login
        findViewById<Button>(R.id.login_button).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()
            loginUser(username, password)
        }
        // get input from user and prepare signup
        findViewById<Button>(R.id.signupButton).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()
            signUpUser(username, password)
        }
    }
    private fun signUpUser(username: String, password: String)
    {
        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // Hooray! Let them use the app now.
                Log.i(TAG, "Successfully logged in user")
                goToMainActivity()
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                e.printStackTrace() // log an exception
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT)
            }
        }
    }



    // Make network call to login user; make in background thread instead of main thread so user can still interact with app
    private fun loginUser(username: String, password: String)
    {
        ParseUser.logInInBackground(username, password, ({ user, e ->
            if (user != null) {
                // Hooray!  The user is logged in.
                Log.i(TAG, "Successfully logged in user")
                goToMainActivity()
            } else {
                // Signup failed.  Look at the ParseException to see what happened.
                e.printStackTrace() // log an exception
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show()
            }})
        )
    }

    // After login send to main activity
    private fun goToMainActivity() {
        // navigates to main activity
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        // closes login activity
        finish()
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}