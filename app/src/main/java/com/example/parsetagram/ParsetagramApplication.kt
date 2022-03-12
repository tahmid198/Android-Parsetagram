package com.example.parsetagram

import android.app.Application
import com.parse.Parse

// App call for Parse initialize whenever our app launches for the first time
    class ParsetagramApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            Parse.initialize(
                Parse.Configuration.Builder(this)
                    .applicationId(getString(R.string.back4app_app_id))
                    .clientKey(getString(R.string.back4app_client_key))
                    .server(getString(R.string.back4app_server_url))
                    .build());
        }
    }



// Note: In android manifest, within Application brackets make sure to tell android which application file to use
// ---> android:name=".ParsetagramApplication"