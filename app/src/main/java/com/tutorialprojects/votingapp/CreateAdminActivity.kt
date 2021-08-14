package com.tutorialprojects.votingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateAdminActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    
    var txtAdminName: androidx.appcompat.widget.AppCompatEditText? = null
    var txtAdminFaculty: androidx.appcompat.widget.AppCompatSpinner? = null
    var txtAdminUsername: androidx.appcompat.widget.AppCompatEditText? = null
    var txtAdminPassword: androidx.appcompat.widget.AppCompatEditText? = null
    var txtAdminCode: androidx.appcompat.widget.AppCompatEditText? = null

    var btnCreateAdmin: androidx.appcompat.widget.AppCompatImageButton? = null

    private val scaleAnimation: ScaleAnimation = ScaleAnimation(
        1f,
        0.8f,
        1f,
        0.8f,
        50f,
        50f
    )

    var aname: String? = null
    var afaculty: String? = null
    var ausername: String? = null
    var apassword: String? = null
    var acode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_admin)

        connectingWidgets()

        scaleAnimation.duration = 250
        scaleAnimation.setInterpolator(this, android.R.interpolator.cycle)

        btnCreateAdmin!!.setOnClickListener {
            btnCreateAdmin!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    createAdmin()
                }
            })
        }
    }

    private fun connectingWidgets() {
        txtAdminName = findViewById(R.id.txtAdminName)
        txtAdminFaculty = findViewById(R.id.txtAdminFaculty)
        txtAdminUsername = findViewById(R.id.txtAdminUsername)
        txtAdminPassword = findViewById(R.id.txtAdminPassword)
        txtAdminCode = findViewById(R.id.txtAdminCode)

        btnCreateAdmin = findViewById(R.id.btnCreateAdmin)
    }

    private fun createAdmin() {
        aname = txtAdminName!!.text.toString()
        afaculty = txtAdminFaculty!!.selectedItem.toString()
        ausername = txtAdminUsername!!.text.toString()
        apassword = txtAdminPassword!!.text.toString()
        acode = txtAdminCode!!.text.toString()

        if (aname!!.isEmpty() || ausername!!.isEmpty() || apassword!!.isEmpty() || acode!!.isEmpty()) {
            Toast.makeText(this, "You Can't Leave Anything Empty", Toast.LENGTH_SHORT).show()
            return
        }

        when (afaculty) {
            "Communication" -> {
                if (acode != COMMUNICATION_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "Dentistry" -> {
                if (acode != DENTISTRY_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "Engineering and National Sciences" -> {
                if (acode != ENGINEERING_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "Health Sciences" -> {
                if (acode != HEALTH_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "Humanities and Social Sciences" -> {
                if (acode != HUMANITIES_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "Medicine" -> {
                if (acode != MEDICINE_CODE) {
                    Toast.makeText(this, "Invalid Faculty Code", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        checkUsername(ausername!!)
    }

    private fun checkUsername(username: String) {
        db.collection("Admins")
            .whereEqualTo("_username", username)
            .get()
            .addOnSuccessListener {
                if (it.size() == 0) {
                    pushAdmin()
                } else {
                    Toast.makeText(this, "Username Already Exists", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pushAdmin() {
        val admin = hashMapOf(
            "_name" to aname,
            "_faculty" to afaculty,
            "_username" to ausername,
            "_password" to apassword,
            "_code" to acode
        )

        db.collection("Admins")
            .add(admin)
            .addOnSuccessListener {
                Toast.makeText(this, "New Admin Has Been Created", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }
}