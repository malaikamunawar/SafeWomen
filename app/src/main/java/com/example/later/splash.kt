package com.example.later

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
class splash : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        sharedEditor = sharedPreferences.edit()

//        val text = findViewById<TextView>(R.id.textView)
//        val icon = findViewById<ImageView>(R.id.imageView)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            if (isUserSignedIn()==true) {
                navigateToDashboard()
            } else {
                if (isUserSignedIn() == true) {
                    navigateToSignInScreen()
                }
                else{
                    navigateToSignInScreen()
                }
            }
    }, 2500)

    }
    private fun isFirstTimeLaunch(): Boolean {
        return if (sharedPreferences.getBoolean("firstTime", true)) {
            sharedEditor.putBoolean("firstTime", false)
            sharedEditor.commit()
            sharedEditor.apply()
            true
        } else {
            false
        }
    }


//    private fun isUserSignedIn(): Boolean {
//        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
//        return prefs.getBoolean(KEY_USER_SIGNED_IN, false)
//    }
    private fun isUserSignedIn(): Boolean {
    val user= FirebaseAuth.getInstance().getCurrentUser()
    if (user!=null){
        return true
    }
    else{
        return false
    }
    }


    private fun navigateToSignInScreen() {
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, dashboard::class.java)
        startActivity(intent)
        finish()
    }
}