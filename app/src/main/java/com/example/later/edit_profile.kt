package com.example.later

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth


class edit_profile : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var nametf: EditText
    private lateinit var phonetf: EditText
    private lateinit var contacttf: EditText
    private lateinit var contactnotf: EditText
    private lateinit var relationtf: EditText
    private lateinit var emailtf: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        nametf = findViewById(R.id.tfname)
        phonetf = findViewById(R.id.tfphone)
        contacttf = findViewById(R.id.tfemergency)
        contactnotf = findViewById(R.id.tfemergencyno)
        relationtf = findViewById(R.id.tfrelation)

        auth = FirebaseAuth.getInstance()

        val savebtn = findViewById<Button>(R.id.save)
        val signout = findViewById<Button>(R.id.signout)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        firebaseDatabase = FirebaseDatabase.getInstance()

        val uid = intent.getStringExtra("userId").toString()

        readUserDataAndPopulateFields(uid)

        savebtn.setOnClickListener {
            saveData()
        }

        signout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

    }

    private fun saveData(){
        var iname = nametf.text.toString()
        var iphone = phonetf.text.toString()
        var icontact = contacttf.text.toString()
        var icontactno = contactnotf.text.toString()
        var irelation = relationtf.text.toString()

        if (iname.isEmpty()){
            nametf.error = "Please fill all fields"
        }
        if (iphone.isEmpty()){
            phonetf.error = "Please fill all fields"
        }
        if (icontact.isEmpty()){
            contacttf.error = "Please fill all fields"
        }
        if (icontactno.isEmpty()){
            contactnotf.error = "Please fill all fields"
        }
        if (irelation.isEmpty()){
            relationtf.error = "Please fill all fields"
        }

        val uid = intent.getStringExtra("userId").toString()
        val u = user( uid, iname, iphone, icontact, icontactno, irelation)

        dbRef.child(uid).setValue(u)
            .addOnCompleteListener{
                Toast.makeText(this, "Data Inserted Successfully.", Toast.LENGTH_SHORT).show()
            }

            .addOnFailureListener{
                Toast.makeText(this, "Data not Inserted.", Toast.LENGTH_SHORT).show()
            }


        val intent  = Intent(this, dashboard::class.java)
        val extras = Bundle()
        extras.putString("userId", uid)

        intent.putExtras(extras)
        startActivity(intent )



    }

    private fun readUserDataAndPopulateFields(userId: String) {
        val userRef = dbRef.child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(user::class.java)

                userData?.let {
                    // Populate your text fields using the retrieved user data
                    nametf.setText(it.username)
                    phonetf.setText(it.userphone)
                    contacttf.setText(it.usercontact)
                    contactnotf.setText(it.usercontactno)
                    relationtf.setText(it.userrelation)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle read error
            }
        })
    }


//    private fun getData() {
//        dbRef?.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.e("0000", "onDataChange: $snapshot")
//                for (ds in snapshot.children){
//                    val id = ds.key
//                    val fbname = ds.child("username").value.toString()
//                    val fbcontact = ds.child("userecontact").value.toString()
//                    val fbcontactno = ds.child("userecontactno").value.toString()
//                    val fbphone = ds.child("userphone").value.toString()
//                    val fbrelation = ds.child("username").value.toString()
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Error", "db error")
//            }
//        })
//    }

}