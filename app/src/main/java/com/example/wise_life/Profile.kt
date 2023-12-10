package com.example.wise_life

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Profile : AppCompatActivity() {

    private lateinit var userPhoto: ImageView
    private lateinit var nameUser: TextView
    private lateinit var age: TextView
    private lateinit var userEmail: TextView
    private lateinit var goals: TextView
    private lateinit var weight: TextView
    private lateinit var height: TextView
    private lateinit var bmi: TextView
    private lateinit var backButton: ImageButton
    private lateinit var btn_customize: RelativeLayout

    //    private lateinit var setting: ImageView
    private lateinit var signout: RelativeLayout

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var timer: CountDownTimer
    private var sessionOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userPhoto = findViewById(R.id.user_profile)
        nameUser = findViewById(R.id.user_name)
        age = findViewById(R.id.userAge)
        userEmail = findViewById(R.id.email)
        goals = findViewById(R.id.goal)
        weight = findViewById(R.id.weight)
        height = findViewById(R.id.height)
        bmi = findViewById(R.id.bmi)
        backButton = findViewById(R.id.arrow_back)
        btn_customize = findViewById(R.id.btn_Customize)
//        setting = findViewById(R.id.setting)
        signout = findViewById(R.id.btn_SignOut)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!

        // Add this line to get a reference to your Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference

        // Check if the user is not authenticated, redirect to MainActivity
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        backButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
        }

        btn_customize.setOnClickListener {
            val intent = Intent(this, Customization::class.java)
            startActivity(intent)
            finish()
        }

        if (user != null) {
            val imageUri: Uri? = user?.photoUrl
            val name = user?.displayName
            val emailId = user?.email
            val email = user?.email

            nameUser.text = "$name"
            // userPhoto.setImageURI(imageUri)
            userEmail.text = "$email"
        }


        timer = object : CountDownTimer(300000, 1000) { // Set session timeout interval here
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                sessionOut = true
            }
        }

        signout.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(this@Profile, "Session Timed Out!!.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Profile, MainActivity::class.java))
        }

        Log.d("Profile", "User ID: ${user!!.uid}")
        // Call a method to retrieve and update the personalization data
        retrieveAndShowPersonalizationData(user!!.uid)
    }

    override fun onPause() {
        super.onPause()
        // Start timer on inactivity
        Log.i("Main", "Timer started!")
        timer.start()
    }

    override fun onResume() {
        super.onResume()

        // If the user comes back after session timeout or from HomePage, update the user
        user = mAuth.currentUser

        if (sessionOut || user == null) {
            // Session timed out or user not authenticated, redirect to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // User is authenticated, update the UI or perform necessary actions
        }

        // Reset sessionOut flag
        sessionOut = false

        // If user does some activity then cancel timer
        if (timer != null) {
            Log.i("Main", "Timer stopped!")
            timer.cancel()
        }
        // If the user comes back after session timeout then redirect to login page
        if (sessionOut) {
            mAuth.signOut()
            Toast.makeText(this@Profile, "Session Timed Out!!.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Profile, MainActivity::class.java))
        }
    }

    private fun retrieveAndShowPersonalizationData(userId: String) {
        // Use the reference to query the database for the user's personalization data
        val personalizationRef = databaseReference.child("Personalizations").child(userId)

        personalizationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the data exists for the specified user ID
                if (dataSnapshot.exists()) {
                    // Retrieve the weight, height, age, goal, & BMI data
                    val weightValue = dataSnapshot.child("currWeight").value.toString()
                    val heightValue = dataSnapshot.child("currHeight").value.toString()
                    val ageUser = dataSnapshot.child("age").value.toString()
                    val goal = dataSnapshot.child("goal").value.toString()

                    // Update the corresponding TextViews with the retrieved data
                    weight.text = "$weightValue kg"
                    height.text = "$heightValue cm"
                    age.text = "$ageUser years old"
                    goals.text = "$goal"

                } else {
                    // Handle the case where the personalization data doesn't exist
                    Log.d("Profile", "Personalization data not found for user: $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during the database query
                Log.e("Profile", "Error retrieving personalization data: $databaseError")
            }
        })
    }

}