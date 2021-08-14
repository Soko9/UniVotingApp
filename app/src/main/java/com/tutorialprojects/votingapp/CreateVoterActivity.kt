package com.tutorialprojects.votingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateVoterActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    var txtVoterName: androidx.appcompat.widget.AppCompatEditText? = null
    var txtVoterDescription: androidx.appcompat.widget.AppCompatEditText? = null

    var rdbMale: RadioButton? = null
    var rdbFemale: RadioButton? = null

    var txtDOB: androidx.appcompat.widget.AppCompatSpinner? = null
    var txtCountry: androidx.appcompat.widget.AppCompatSpinner? = null
    var txtVoterFaculty: androidx.appcompat.widget.AppCompatSpinner? = null
    var txtDepartment: androidx.appcompat.widget.AppCompatSpinner? = null

    var txtVoterUsername: androidx.appcompat.widget.AppCompatEditText? = null
    var txtVoterPassword: androidx.appcompat.widget.AppCompatEditText? = null

    var btnCreateVoter: androidx.appcompat.widget.AppCompatImageButton? = null

    private val scaleAnimation: ScaleAnimation = ScaleAnimation(
        1f,
        0.8f,
        1f,
        0.8f,
        50f,
        50f
    )

    var vname: String? = null
    var vdesc: String? = null
    var vgender: String? = null
    var vdob: Int? = null
    var vcountry: String? = null
    var vfaculty: String? = null
    var vdepartment: String? = null
    var vusername: String? = null
    var vpassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_voter)

        connectWidgets()

        scaleAnimation.duration = 250
        scaleAnimation.setInterpolator(this, android.R.interpolator.cycle)

        rdbMale!!.setOnClickListener {
            vgender = "Male"
        }

        rdbFemale!!.setOnClickListener {
            vgender = "Female"
        }

        txtVoterFaculty!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (parent!!.getItemAtPosition(position).toString()) {
                    "Communication" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.com_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                    "Dentistry" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.den_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                    "Engineering and National Sciences" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.eng_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                    "Health Sciences" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.hlt_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                    "Humanities and Social Sciences" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.hum_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                    "Medicine" -> {
                        val adapter = ArrayAdapter.createFromResource(this@CreateVoterActivity, R.array.med_department, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
                        txtDepartment!!.adapter = adapter
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnCreateVoter!!.setOnClickListener {
            btnCreateVoter!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    createVoter()
                }
            })
        }
    }

    private fun connectWidgets() {
        txtVoterName = findViewById(R.id.txtVoterName)
        txtVoterDescription = findViewById(R.id.txtVoterDescription)
        rdbMale = findViewById(R.id.rdbMale)
        rdbFemale = findViewById(R.id.rdbFemale)
        txtDOB = findViewById(R.id.txtDOB)
        txtCountry = findViewById(R.id.txtCountry)
        txtVoterFaculty = findViewById(R.id.txtVoterFaculty)
        txtDepartment = findViewById(R.id.txtDepartment)
        txtVoterUsername = findViewById(R.id.txtVoterUsername)
        txtVoterPassword = findViewById(R.id.txtVoterPassword)
        btnCreateVoter = findViewById(R.id.btnCreateVoter)
    }

    private fun createVoter() {
        vname = txtVoterName!!.text.toString()
        vdesc = txtVoterDescription!!.text.toString()
        vdob = txtDOB!!.selectedItem.toString().toInt()
        vcountry = txtCountry!!.selectedItem.toString()
        vfaculty = txtVoterFaculty!!.selectedItem.toString()
        vdepartment = txtDepartment!!.selectedItem.toString()
        vusername = txtVoterUsername!!.text.toString()
        vpassword = txtVoterPassword!!.text.toString()

        if (vname!!.isEmpty() || vdesc!!.isEmpty() || vusername!!.isEmpty() || vpassword!!.isEmpty()) {
            Toast.makeText(this, "You Can't Leave Anything Empty", Toast.LENGTH_SHORT).show()
            return
        }
        checkUsername(vusername!!)
    }

    private fun checkUsername(username: String) {
        db.collection("Voters")
            .whereEqualTo("_username", username)
            .get()
            .addOnSuccessListener {
                if (it.size() == 0) {
                    pushVoter()
                } else {
                    Toast.makeText(this, "Username Already Exists", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pushVoter() {
        vgender = if (rdbMale!!.isChecked) {
            "Male"
        } else {
            "Female"
        }
        val voter = hashMapOf(
                "_name" to vname,
                "_description" to vdesc,
                "_gender" to vgender,
                "_dob" to vdob,
                "_country" to vcountry,
                "_faculty" to vfaculty,
                "_department" to vdepartment,
                "_username" to vusername,
                "_password" to vpassword,
                "_voted" to false,
                "_votes" to 0
        )

        db.collection("Voters")
            .add(voter)
            .addOnSuccessListener {
                Toast.makeText(this, "New Voter Has Been Created", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }
}