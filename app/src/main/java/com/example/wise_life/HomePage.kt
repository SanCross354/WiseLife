package com.example.wise_life

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomePage : AppCompatActivity(), OnItemClickListener {

    private lateinit var dietPlanRecycler: RecyclerView
    private lateinit var dietPlanAdapter: DietPlanAdapter // Corrected the type
    private lateinit var dietPlans: ArrayList<DietPlanHelperClass>

    private var progress = 0 //Circle Progress Bar
    private var progressCarb = 0 //Horizontal Progress Carb
    private var progressProtein = 0 //Horizontal Progress Protein
    private var progressFat = 0 //Horizontal Progress Fat

    private lateinit var profile: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var eaten: TextView
    private lateinit var burned: TextView
    private lateinit var numberProgress: TextView
    private lateinit var carbs: TextView
    private lateinit var proteins: TextView
    private lateinit var fats: TextView
//    private lateinit var add: Button
//    private lateinit var substract: Button
    private lateinit var updateWeight: TextView

    //Horizontal Progress Bar
    private lateinit var carbBar: ProgressBar
    private lateinit var proteinBar: ProgressBar
    private lateinit var fatBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var timer: CountDownTimer
    private var sessionOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //Hooks
        dietPlanRecycler = findViewById(R.id.featured_recycler)

        //Generate Diet Plan Card (5 item)
        DietPlanRecycler()

        profile = findViewById(R.id.profile)

        //Progress Bar
        progressBar = findViewById(R.id.progress_bar)  //Circle Progress Bar
        carbBar = findViewById(R.id.horizontal_barCarb)
        proteinBar = findViewById(R.id.horizontal_barProtein)
        fatBar = findViewById(R.id.horizontal_barFat)

        //Number showed in progress
        eaten = findViewById(R.id.eaten)
        burned = findViewById(R.id.burned)
        numberProgress = findViewById(R.id.numberProgress)
        carbs = findViewById(R.id.carbs)
        proteins = findViewById(R.id.protein)
        fats = findViewById(R.id.fat)

        //Button to update progress (still testing-only)
//        add = findViewById(R.id.add)
//        substract = findViewById(R.id.substract)

        // Add this line to get a reference to your Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference

        //Update Current Weight
        updateWeight = findViewById(R.id.updateWeight)

        //Utilizing Firebase function
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser

        // Check if the user is not authenticated, redirect to MainActivity
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }


        updateWeight.setOnClickListener {
            showUpdateCurrentWeightDialog(this)
        }

//        add.setOnClickListener {
//            Log.d("HomePage", "add button clicked")
//            retrieveDataForCalorieNeed(user!!.uid) { calorieNeeds ->
//                val dailyCarb = carbNeed(calorieNeeds)
//                val dailyProtein = proteinNeed(calorieNeeds)
//                val dailyFat = fatNeed(calorieNeeds)
//
//                Log.d("HomePage", "calorieNeeds: $calorieNeeds, dailyCarb: $dailyCarb, dailyProtein: $dailyProtein, dailyFat: $dailyFat")
//
//                if (progress <= calorieNeeds && progressCarb <= dailyCarb
//                    && progressProtein <= dailyProtein && progressFat <= dailyFat
//                ) {
//                    progress += 200
//                    progressCarb += 200
//                    progressProtein += 200
//                    progressFat += 200
//                    updateProgressBarCalori()
//                }
//            }
//            Log.d("HomePage", "updateProgressBarCalori() called after add button click")
//        }
//
//        substract.setOnClickListener {
//            Log.d("HomePage", "subtract button clicked")
//            if (progress >= 10) {
//                progress -= 10
//                progressCarb -= 10
//                progressProtein -= 10
//                progressFat -= 10
//                updateProgressBarCalori()
//            }
//            Log.d("HomePage", "updateProgressBarCalori() called after subtract button click")
//        }

        timer = object : CountDownTimer(300000, 1000) { // Set session timeout interval here
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                sessionOut = true
            }
        }

        retrieveDataForCalorieNeed(user!!.uid) { calorieNeeds ->
            numberProgress.text = calorieNeeds.toString()
        }
    }

    override fun onItemClick(position: Int) {
        // Handle item click here
        // Check if dietPlans is initialized before accessing it
        dietPlans?.let {
            if (position in it.indices) {
                val clickedItem: DietPlanHelperClass = it[position]

                // Check the title to determine which diet plan was clicked
                if (clickedItem.title == "Breakfast") {
                    // Navigate to the Breakfast page here
                    val intent = Intent(this@HomePage, Breakfast::class.java)
                    startActivity(intent)
                }

                if (clickedItem.title == "Lunch") {
                    // Navigate to the Lunch page here
                    val intent = Intent(this@HomePage, Lunch::class.java)
                    startActivity(intent)
                }

                if (clickedItem.title == "Dinner") {
                    // Navigate to the Lunch page here
                    val intent = Intent(this@HomePage, Dinner::class.java)
                    startActivity(intent)
                }

                if (clickedItem.title == "Snacks") {
                    // Navigate to the Lunch page here
                    val intent = Intent(this@HomePage, Snack::class.java)
                    startActivity(intent)
                }


                // Add more conditions for other diet plans if needed
            }
        }
    }


    private fun updateProgressBarCalori() {
        Log.d("HomePage", "updateProgressBarCalori() called")
        if (::progressBar.isInitialized && ::carbBar.isInitialized && ::proteinBar.isInitialized && ::fatBar.isInitialized) {
            retrieveDataForCalorieNeed(user!!.uid) { calorieNeeds ->

                // Get the reference to the "Progress" node under the user's UID
                val userProgressReference =
                    FirebaseDatabase.getInstance().reference.child("Progress").child(user!!.uid)
                userProgressReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Get the current values from the Progress node
                            val currentCalorie =
                                dataSnapshot.child("calorie").getValue(Int::class.java) ?: 0
                            val currentCarb =
                                dataSnapshot.child("carb").getValue(Int::class.java) ?: 0
                            val currentProtein =
                                dataSnapshot.child("protein").getValue(Int::class.java) ?: 0
                            val currentFat =
                                dataSnapshot.child("fat").getValue(Int::class.java) ?: 0

                            // Callback with retrieved calorieNeeds value
                            val dailyCarb = carbNeed(calorieNeeds)
                            val dailyProtein = proteinNeed(calorieNeeds)
                            val dailyFat = fatNeed(calorieNeeds)

                            // Set the limits based on the calculated values
                            val carbLimit = dailyCarb
                            val proteinLimit = dailyProtein
                            val fatLimit = dailyFat

                            // Update progress bars with the retrieved values
                            progressBar.max = calorieNeeds
                            carbBar.max = carbLimit
                            proteinBar.max = proteinLimit
                            fatBar.max = fatLimit

                            // Update progress values, ensuring they don't exceed the limits
                            progress = currentCalorie.coerceIn(0, calorieNeeds)
                            progressCarb = currentCarb.coerceIn(0, carbLimit)
                            progressProtein = currentProtein.coerceIn(0, proteinLimit)
                            progressFat = currentFat.coerceIn(0, fatLimit)

                            progressBar.progress = progress
                            carbBar.progress = progressCarb
                            proteinBar.progress = progressProtein
                            fatBar.progress = progressFat

                            var calorieLeft = calorieNeeds - currentCalorie

                            eaten.text = "$progress"
                            numberProgress.text = "$calorieLeft"
                            carbs.text = "$progressCarb/$carbLimit g"
                            proteins.text = "$progressProtein/$proteinLimit g"
                            fats.text = "$progressFat/$fatLimit g"
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Firebase", "Error reading progress data: ${databaseError.message}")
                    }
                })
            }
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
        } else {
            // User is authenticated, update the UI or perform necessary actions
            updateProgressBarCalori()
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
            Toast.makeText(this@HomePage, "Session Timed Out!!.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@HomePage, MainActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        // On closing application sign out user
//        mAuth.signOut()
    }

    //For generate card of diet plans using DietPlan Recycler
    private fun DietPlanRecycler() {
        dietPlanRecycler.setHasFixedSize(true)
        dietPlanRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Initialize dietPlans before using it
        dietPlans = ArrayList()

        dietPlans?.apply {
            add(DietPlanHelperClass(R.drawable.breakfast, "Breakfast", "Recommended 508 - 763 kcal"))
            add(DietPlanHelperClass(R.drawable.lunch_time, "Lunch", "Recommended 763 - 1017 kcal"))
            add(DietPlanHelperClass(R.drawable.dinner, "Dinner", "Recommended 763 - 1017 kcal"))
            add(DietPlanHelperClass(R.drawable.snack, "Snacks", "Recommended 127 - 254 kcal"))
            add(DietPlanHelperClass(R.drawable.exercise, "Exercise", "Recommended: 30 min"))
        }

        dietPlanAdapter = DietPlanAdapter(dietPlans!!, this)
        dietPlanRecycler.adapter = dietPlanAdapter

        val gradient1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(0xffeff400.toInt(), 0xffaff600.toInt())
        )
    }


    fun showUpdateCurrentWeightDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.goal_weight_card)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val newWeight = dialog.findViewById(R.id.numberPickerWeight) as NumberPicker
        val btn_updateWeight = dialog.findViewById(R.id.btn_updateWeight) as Button


        btn_updateWeight.setOnClickListener {
            //Reference in AdapterEdit at project FirebaseDatabase
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun retrieveDataForCalorieNeed(userID: String, callback: (calorieNeeds: Int) -> Unit) {
        val personalizationRef = databaseReference.child("Personalizations").child(userID)

        personalizationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val weightValue = (dataSnapshot.child("currWeight").value as? String)?.toDouble() ?: 0.0
                    val heightValue = (dataSnapshot.child("currHeight").value as? String)?.toDouble() ?: 0.0
                    val ageUser = (dataSnapshot.child("age").value as? String)?.toDouble() ?: 0.0
                    val exerciseFrequent = (dataSnapshot.child("exercise").value as? Long)?.toInt() ?: 0

                    Log.d("HomePage", "weightValue: $weightValue, heightValue: $heightValue, ageUser: $ageUser, exerciseFrequent: $exerciseFrequent")

                    var weight = weightValue * 13.7
                    var height = heightValue * 5
                    var age = ageUser * 6.8
                    var exercise: Double

                    if (exerciseFrequent == 0) {
                        exercise = 1.2
                    } else if (exerciseFrequent < 4) {
                        exercise = 1.375
                    } else if (exerciseFrequent < 6) {
                        exercise = 1.55
                    } else if (exerciseFrequent < 8) {
                        exercise = 1.725
                    } else {
                        exercise = 1.9
                    }

                    val calorieNeeds = ((66 + weight + height + age) * exercise).toInt()

                    Log.d("HomePage", "calorieNeeds: $calorieNeeds")
                    callback(calorieNeeds)
                } else {
                    Log.d("Profile", "Personalization data not found for user: $userID")
                    callback(0) // or handle the absence of data in a way that makes sense for your use case
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Profile", "Error retrieving personalization data: $databaseError")
                callback(0) // or handle the error in a way that makes sense for your use case
            }
        })
    }



    private fun carbNeed(calorieNeeds: Int) : Int {
        return (0.6 * calorieNeeds).toInt()
    }

    private fun proteinNeed(calorieNeeds: Int) : Int {
        return (0.15 * calorieNeeds).toInt()
    }

    private fun fatNeed(calorieNeeds: Int) : Int {
        return (0.15 * calorieNeeds).toInt()
    }
}
