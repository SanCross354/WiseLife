package com.example.wise_life

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var signIn: Button
    private lateinit var signUp: TextView

    //    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        signIn = findViewById(R.id.sign_in)
        signUp = findViewById(R.id.sign_up)

        signIn.setOnClickListener {
            val email: String = editTextEmail.text.toString()
            val password: String = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }



//        signIn.setOnClickListener {
//            val email: String = editTextEmail.text.toString().trim()
//            val password: String = editTextPassword.text.toString().trim()
//
//            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
//                Toast.makeText(this, "Enter Email & Password!!", Toast.LENGTH_SHORT).show()
//            } else {
//                mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this) { task ->
//                        if (task.isSuccessful) {
//                            // Check if the user's email is verified
//                            val currentUser = mAuth.currentUser
//                            if (currentUser != null) {
//                                currentUser.reload().addOnCompleteListener { reloadTask ->
//                                    if (reloadTask.isSuccessful) {
//                                        if (currentUser.isEmailVerified) {
//                                            // Sign in success and email is verified, update UI with the signed-in user's information
//                                            updateUI(currentUser)
//                                        } else {
//                                            // Email not verified
//                                            Toast.makeText(this, "Your Email is not verified.", Toast.LENGTH_SHORT).show()
//                                            mAuth.signOut() // Sign out the user in case of unverified email
//                                        }
//                                    } else {
//                                        // Reload task failed
//                                        Toast.makeText(this, "Reload failed: " + reloadTask.exception, Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            } else {
//                                // User is null, handle the error or sign out
//                                Toast.makeText(this, "User is null.", Toast.LENGTH_SHORT).show()
//                                mAuth.signOut()
//                            }
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(
//                                this,
//                                "Authentication failed: " + task.exception,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            updateUI(null)
//                        }
//                    }
//            }
//        }

        signUp.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Check if the user is already authenticated
//        val currentUser = mAuth.currentUser
//        if (currentUser != null) {
//            // User is already authenticated, proceed to HomePage
//            startActivity(Intent(this, HomePage::class.java))
//            finish()
//        }
    }

//    private fun updateUI(user: FirebaseUser?) {
//        this.user = mAuth.currentUser
//        /*-------- Check if user is already logged in or not--------*/
//        if (this.user != null) {
//            /*------------ If user's email is verified then access login -----------*/
//            if (this.user!!.isEmailVerified) {
//                Toast.makeText(this, "Login Success.", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, HomePage::class.java))
//            } else {
//                Toast.makeText(this, "Your Email is not verified.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}
