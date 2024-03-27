package com.example.later

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AlertDialog

class dashboard : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dbRef: DatabaseReference
    val LOCATION_PERMISSION_REQUEST_CODE = 1001
    var uid: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        uid = intent.getStringExtra("userId").toString()

        val uemail = intent.getStringExtra("userEmail").toString()
        val settings = findViewById<ImageButton>(R.id.imageButton)
        val callpolice = findViewById<Button>(R.id.callpolice)
        val callcontact = findViewById<Button>(R.id.callcontact)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // getLastLocation()
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            showLocationServicesAlertDialog(this)
        }

//        if (checkLocationPermissions()) {
//            // Permission granted, proceed to get location
//            getLastLocation()
//        } else {
//            requestLocationPermissions()
//        }

        callpolice.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "15")
            startActivity(dialIntent)
        }
        callcontact.setOnClickListener {
            callContact(uid)
        }

        settings.setOnClickListener {

            val intent = Intent(this, edit_profile::class.java)
            val extras = Bundle()
            extras.putString("userId", uid)
            extras.putString("userEmail", uemail)
            intent.putExtras(extras)
            startActivity(intent)
        }

        val sosbtn = findViewById<Button>(R.id.sos)
        sosbtn.setOnClickListener {
            getLastLocation()
        }
    }


    fun onWhatsAppShareClicked(context: Context, mobileNumber: String, message: String) {
        var encodedMessage = Uri.encode(message)
        val url =
            "https://api.whatsapp.com/send?phone=${mobileNumber}&text=$encodedMessage"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            this.data = Uri.parse(url)
            this.`package` = "com.whatsapp"
        }
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            //whatsapp not installed
        }
    }

    private fun callContact(userId: String) {
        var emergencyNumber: String = ""
        try {
            val userRef = dbRef.child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(user::class.java)

                    userData?.let {
                        emergencyNumber = it.usercontactno.toString()
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.data = Uri.parse("tel:$emergencyNumber")
                        startActivity(dialIntent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.message
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun SendAlert(userId: String, url: String?): String {
        var emergencyNumber: String = ""
        var uname: String = ""
        try {
            val userRef = dbRef.child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(user::class.java)

                    userData?.let {
                        emergencyNumber = it.usercontactno.toString()
                        uname = it.username.toString()
                        val message: String = "I need help! This is my location : \n" + url +"\n This message was sent to you by SafeWomen App, you were registered as $uname's emergency contact."
                        onWhatsAppShareClicked(this@dashboard, emergencyNumber.toString(), message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.message
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emergencyNumber.toString()
    }



    private fun getLastLocation() {
        var locationUrl: String? = null
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val lati_string: String = latitude.toString()
                        val long_string: String = longitude.toString()
                        //Toast.makeText(this, lati_string, Toast.LENGTH_SHORT).show()
                        locationUrl = "https://www.google.com/maps?q=${lati_string},${long_string}"
                        SendAlert(uid, locationUrl)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "onFailure" )
                    locationUrl = "https://www.google.com/maps?q="
                }
        }
        else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Pemission Denied" )
            locationUrl = "permission problem"
            showLocationServicesAlertDialog(this)
        }

    }
    fun showLocationServicesAlertDialog(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.apply {
                setTitle("Turn On Location Services")
                setMessage("Dear User, Please turn on location services to use this application. We use your location to send it to your emergency contact in case of any emergency.")
                setPositiveButton("Settings") { _, _ ->
                    val settingsIntent =
                        Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(settingsIntent)
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                setCancelable(false)
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}