package com.example.wise_life

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class Lunch : AppCompatActivity(), MenuAdapter.MenuClickListener {

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var timer: CountDownTimer
    private var sessionOut = false

    private lateinit var menuRecycler: RecyclerView
    private lateinit var adapter: MenuAdapter

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        backButton = findViewById(R.id.backButton)

        // Hooks
        menuRecycler = findViewById(R.id.menu_recycler)

        // Generate Diet Plan Card (5 items)
        menuPlanRecycler()

        // Utilizing Firebase function
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser

        // Check if the user is not authenticated, redirect to MainActivity
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        timer = object : CountDownTimer(300000, 1000) { // Set session timeout interval here
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                sessionOut = true
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        // Start timer on inactivity
        Log.i("Main", "Timer started!")
        timer.start()
    }

    override fun onResume() {
        super.onResume()

        // If the user comes back after session timeout or from Profile page, update the user
        user = mAuth.currentUser

        if (sessionOut || user == null) {
            // Session timed out or user not authenticated, redirect to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Reset sessionOut flag
        sessionOut = false

        // If the user does some activity then cancel the timer
        if (timer != null) {
            Log.i("Main", "Timer stopped!")
            timer.cancel()
        }
        // If the user comes back after session timeout then redirect to login page
        if (sessionOut) {
            mAuth.signOut()
            Toast.makeText(this@Lunch, "Session Timed Out!!.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Lunch, MainActivity::class.java))
        }
    }

    private fun menuPlanRecycler() {
        menuRecycler.setHasFixedSize(true)
        menuRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val menu = ArrayList<MenuHelperClass>()

        menu.add(MenuHelperClass(R.drawable.boiled_egg, "Boiled Egg", "155", "1", "12", "10"))
        menu.add(MenuHelperClass(R.drawable.chicken_sandwich, "Chicken Sandwich", "320", "40", "20", "10"))
        menu.add(MenuHelperClass(R.drawable.vegetable_salad, "Vegetable Salad", "120", "10", "5", "8"))
        menu.add(MenuHelperClass(R.drawable.fruit_salad, "Fruit Salad", "80", "20", "1", "0"))

        adapter = MenuAdapter(menu, this)
        menuRecycler.adapter = adapter
    }

    override fun onMenuAddClicked(menu: MenuHelperClass) {
        // Handle menu add click here
        // Extract calorie, carb, protein, and fat from the menu
        val calorieStr = menu.calorie
        val carbStr = menu.carb
        val proteinStr = menu.protein
        val fatStr = menu.fat

        // Convert the strings to integers
        val calorie = calorieStr.toInt()
        val carb = carbStr.toInt()
        val protein = proteinStr.toInt()
        val fat = fatStr.toInt()

        // Now you have the values as integers, and you can use them in your HomePage activity
        val homePageIntent = Intent(this@Lunch, HomePage::class.java)
        homePageIntent.putExtra("CALORIE", calorie)
        homePageIntent.putExtra("CARB", carb)
        homePageIntent.putExtra("PROTEIN", protein)
        homePageIntent.putExtra("FAT", fat)

        // Update progress data in Firebase
        updateProgressInFirebase(calorie, carb, protein, fat)

        startActivity(homePageIntent)
    }

    private fun updateProgressInFirebase(calorie: Int, carb: Int, protein: Int, fat: Int) {
        // Get the reference to the "Progress" node under the user's UID
        val userProgressReference = FirebaseDatabase.getInstance().reference.child("Progress").child(user!!.uid)

        // Use a listener to check if the data already exists
        userProgressReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentCalorie = dataSnapshot.child("calorie").getValue(Int::class.java) ?: 0
                val currentCarb = dataSnapshot.child("carb").getValue(Int::class.java) ?: 0
                val currentProtein = dataSnapshot.child("protein").getValue(Int::class.java) ?: 0
                val currentFat = dataSnapshot.child("fat").getValue(Int::class.java) ?: 0

                userProgressReference.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        // Accumulate the values
                        val updatedCalorie = currentCalorie + calorie
                        val updatedCarb = currentCarb + carb
                        val updatedProtein = currentProtein + protein
                        val updatedFat = currentFat + fat

                        // Update the values in the transaction
                        mutableData.child("calorie").value = updatedCalorie
                        mutableData.child("carb").value = updatedCarb
                        mutableData.child("protein").value = updatedProtein
                        mutableData.child("fat").value = updatedFat

                        // Mark transaction as successful
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                        if (databaseError != null) {
                            // Handle error
                            Log.e("Firebase", "Error updating progress data: ${databaseError.message}")
                        }
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error reading progress data: ${databaseError.message}")
            }
        })
    }
}
