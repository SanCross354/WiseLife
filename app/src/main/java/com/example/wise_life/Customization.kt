package com.example.wise_life

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.MessageFormat
import java.util.Objects

class Customization : AppCompatActivity() {

    //    private lateinit var setting: ImageView
    private lateinit var signout: RelativeLayout

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var timer: CountDownTimer
    private var sessionOut = false

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customization)

        val button = findViewById<MaterialButton>(R.id.btn)
        val textView = findViewById<TextView>(R.id.tv)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!

        timer = object : CountDownTimer(300000, 1000) { // Set session timeout interval here
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                sessionOut = true
            }
        }

        button.setOnClickListener { view: View? ->
            val view1 =
                LayoutInflater.from(this@Customization).inflate(R.layout.dialog_layout, null)
            val editText = view1.findViewById<TextInputEditText>(R.id.editText)
            val alertDialog =
                MaterialAlertDialogBuilder(this@Customization)
                    .setTitle("Goal")
                    .setView(view1)
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface: DialogInterface, i: Int ->
                        textView.setText(
                            MessageFormat.format(
                                "{0}",
                                Objects.requireNonNull<Editable?>(editText.text)
                            )
                        )
                        dialogInterface.dismiss()
                    }.setNegativeButton(
                        "Close"
                    ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                    .create()
            alertDialog.show()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
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
            Toast.makeText(this@Customization, "Session Timed Out!!.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Customization, MainActivity::class.java))
        }
    }
}