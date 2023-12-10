package com.example.wise_life

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

import java.util.UUID

class Register : AppCompatActivity() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var signUp: Button
    private lateinit var signIn: TextView

    private lateinit var rootNode: FirebaseDatabase

    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextName = findViewById(R.id.name)
        editTextUsername = findViewById(R.id.username)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        signIn = findViewById(R.id.sign_in)
        signUp = findViewById(R.id.sign_up)


        rootNode = FirebaseDatabase.getInstance() //to access Database

        //Moving to Login Page if users already have account
        signIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUp.setOnClickListener {
            signUpUser(
                editTextUsername.text.toString(),
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            )
        }

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
    }


    //    Below Code for successful Sign Up Process
    private fun signUpUser(username: String, email: String, password: String) {
        when {
            username.trim() == "" -> Toast.makeText(this@Register, "Please enter Username!!", Toast.LENGTH_SHORT).show()
            email.trim() == "" -> Toast.makeText(this@Register, "Please enter Email!!", Toast.LENGTH_SHORT).show()
            password.trim() == "" -> Toast.makeText(this@Register, "Please enter Password!!", Toast.LENGTH_SHORT).show()
            else -> {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        userProfile()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@Register,
                            "Sign up failed: " + task.exception,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    // For storing username data in Firebase Database (FirebaseUser)
    private fun userProfile() {
        user = mAuth.currentUser!!
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(editTextUsername.text.toString()).build()
            user.updateProfile(profileUpdates).addOnCompleteListener(this) {

                //Save data to Firebase
                setDataToDatabase(getDataPengguna())

                startActivity(Intent(this@Register, MainActivity::class.java))
                finish()
            }
        }
    }

//    /*-------For sending verification email on user's registered email------*/
//    private fun verifyEmailRequest() {
//        user.sendEmailVerification()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(
//                        this@Register,
//                        "Email verification sent on\n" + editTextEmail.text.toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    mAuth.signOut()
//                    startActivity(Intent(this@Register, MainActivity::class.java))
//                } else {
//                    Toast.makeText(
//                        this@Register,
//                        "Sign up Success But Failed to send verification email.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//    }

    //    Retrieve User data from Regist Form
    private fun getDataPengguna(): Users {
        return Users(
            id = user?.uid ?: "",
            name = editTextName.text.toString().trim(),
            username = editTextUsername.text.toString().trim(),
            email = editTextEmail.text.toString().trim(),
            password = editTextPassword.text.toString()
        )
    }

    //    Write data to database
    private fun setDataToDatabase(user: Users) {
        rootNode.getReference("Users").child(user.id).setValue(user)

    }
}
