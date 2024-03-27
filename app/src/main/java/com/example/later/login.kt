package com.example.later


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login : AppCompatActivity() {

    lateinit var client: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signinbtn: Button
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        dbRef = FirebaseDatabase.getInstance().getReference("Users")


        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()



        signinbtn = findViewById(R.id.signup)
        client = GoogleSignIn.getClient(this, options)
        firebaseAuth = FirebaseAuth.getInstance()

        signinbtn.setOnClickListener {
            val intent = client.signInIntent
            startActivityForResult(intent, 10001)
        }
    }

    private fun checkfields(userId: String, callback: (Boolean) -> Unit) {
        val userRef = dbRef.child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(user::class.java)
                var filled = true // Default value

                userData?.let {
                    val userId = it.userid
                    val username = it.username
                    val userphone = it.userphone
                    val usercontact = it.usercontact
                    val usercontactno = it.usercontactno
                    val userrelation = it.userrelation

                    if (username == "" || userphone == "" || usercontact == "" || usercontactno == "" || userrelation == "") {
                        filled = false
                    }

                    callback(filled)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle read error
                callback(false) // Indicate an error condition
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val userId = user.uid
                            val userRef = dbRef.child(userId)

                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userData = snapshot.getValue(com.example.later.user::class.java)

                                    if (userData == null) {
                                        val intent  = Intent(this@login, profile::class.java)
                                        val extras = Bundle()
                                        extras.putString("userId", userId)
                                        intent.putExtras(extras)
                                        startActivity(intent)
                                    }
                                    else{
                                        val intent  = Intent(this@login, dashboard::class.java)
                                        val extras = Bundle()
                                        extras.putString("userId", userId)
                                        intent.putExtras(extras)
                                        startActivity(intent)
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle read error
                                    error.message
                                }
                            })


                            // Call checkfields to determine if all fields are filled
//                            checkfields(userId) { filled ->
//                                if (filled==true) {
//                                    // All fields are filled, navigate to the dashboard
//                                    val dashboardIntent = Intent(this, dashboard::class.java)
//                                    startActivity(dashboardIntent)
//                                } else {
//                                    // Fields are not filled, navigate to the profile screen
//                                    val profileIntent = Intent(this, profile::class.java)
//                                    startActivity(profileIntent)
//                                }
//                            }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "signInWithCredential:failure")
                    }
                }
        }
    }



//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 10001) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            val account = task.getResult(ApiException::class.java)
//            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//            FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d(TAG, "signInWithCredential:success");
//                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//                        val user = FirebaseAuth.getInstance().currentUser
//                        if (user != null) {
//                            val userId = user.uid
//                            val userEmail = user.email
//                            Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(this, userEmail, Toast.LENGTH_SHORT).show()
//
//                        }
//                    } else {
//                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
//                        Log.w(TAG, "signInWithCredential:failure")
//                    }
//
//                }
//
//        }
//    }

}