package com.example.parsetagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parsetagram.fragments.ComposeFragment
import com.example.parsetagram.fragments.FeedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*

/**
 * Let user create a post by taking a photo with their camera
 * */

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        // setOnItemSelectedListener will let you know which specific item was clicked
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {

            // Arrow symbol passes in variable or else we can use generic variable 'it' which is passed by default (for example it.itemId)
                item ->
            var fragmentToShow: Fragment? = null
            when (item.itemId) { // when item is selected

                R.id.action_home -> {
                    // TODO Navigate to home screen / feed fragment
                    fragmentToShow = FeedFragment()
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()

                }
                R.id.action_compose -> {
                    fragmentToShow = ComposeFragment()
                    Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show()

                }
                R.id.action_profile -> {
                    // TODO navigate to profile screen
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }
            }
            if(fragmentToShow != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit() // place whatever is in the container with the fragment we want to show and the call commit so it happens
            }
            // Return true to say that we've handled user intreaction on the item
            true
        }
        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            ParseUser.logOut()
            val currentUser = ParseUser.getCurrentUser() // this will now be null
            goToLoginActivity()
        }

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
