package com.example.later

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class profile : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var nametf: EditText
    private lateinit var phonetf: EditText
    private lateinit var contacttf: EditText
    private lateinit var contactnotf: EditText
    private lateinit var relationtf: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val uemail = intent.getStringExtra("userEmail").toString()

        nametf = findViewById(R.id.tfname)
         phonetf = findViewById(R.id.tfphone)
        contacttf = findViewById(R.id.tfemergency)
        contactnotf = findViewById(R.id.tfemergencyno)
         relationtf = findViewById(R.id.tfrelation)


        val savebtn = findViewById<Button>(R.id.save)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff = rootView.rootView.height - rootView.height
                if (heightDiff > 100) {
                    // Keyboard is open
                    scrollView.isScrollContainer = true
                } else {
                    // Keyboard is closed
                    scrollView.isScrollContainer = false
                }
            }
        })
        savebtn.setOnClickListener {
            saveData()
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

}





