package com.tutorialprojects.votingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.accessibility.CaptioningManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    var rdbStudent: RadioButton? = null
    var rdbAdmin: RadioButton? = null

    var txtUsername: androidx.appcompat.widget.AppCompatEditText? = null
    var txtPassword: androidx.appcompat.widget.AppCompatEditText? = null

    var facultyContainer: LinearLayout? = null
    var txtFacultyCode: androidx.appcompat.widget.AppCompatEditText? = null

    var btnVote: androidx.appcompat.widget.AppCompatImageButton? = null
    var btnCreateCampaign: androidx.appcompat.widget.AppCompatImageButton? = null
    var btnCreate: androidx.appcompat.widget.AppCompatImageButton? = null

    var username: String? = null
    var password: String? = null
    var code: String? = null

    private val scaleAnimation: ScaleAnimation = ScaleAnimation(
            1f,
            0.8f,
            1f,
            0.8f,
            50f,
            50f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectingWidgets()

        scaleAnimation.duration = 250
        scaleAnimation.setInterpolator(this, android.R.interpolator.cycle)

        if (rdbStudent!!.isChecked)
            studentChoice()
        if (rdbAdmin!!.isChecked)
            adminChoice()

        rdbStudent!!.setOnClickListener {
            studentChoice()
        }

        rdbAdmin!!.setOnClickListener {
            adminChoice()
        }

        btnVote!!.setOnClickListener {
            btnVote!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    username = txtUsername!!.text.toString().trim()
                    password = txtPassword!!.text.toString().trim()
                    checkStudentCredits(username!!, password!!)
                }
            })
        }

        btnCreateCampaign!!.setOnClickListener {
            btnCreateCampaign!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    username = txtUsername!!.text.toString().trim()
                    password = txtPassword!!.text.toString().trim()
                    code = txtFacultyCode!!.text.toString().trim()
                    checkAdminCredits(username!!, password!!, code!!)
                }
            })
        }

        btnCreate!!.setOnClickListener {
            btnCreate!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    if (rdbAdmin!!.isChecked) {
                        val intent: Intent = Intent(this@MainActivity, CreateAdminActivity::class.java)
                        startActivity(intent)
                    }
                    if (rdbStudent!!.isChecked) {
                        val intent: Intent = Intent(this@MainActivity, CreateVoterActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        rdbStudent!!.isChecked = true
        studentChoice()
    }

    private fun connectingWidgets() {
        rdbStudent = findViewById(R.id.rdbStudent)
        rdbAdmin = findViewById(R.id.rdbAdmin)

        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)

        facultyContainer = findViewById(R.id.facultyContainer)
        txtFacultyCode = findViewById(R.id.txtFacultyCode)

        btnVote = findViewById(R.id.btnVote)
        btnCreateCampaign = findViewById(R.id.btnCreateCampaign)
        btnCreate = findViewById(R.id.btnCreate)
    }

    private fun studentChoice() {
        facultyContainer!!.visibility = View.GONE

        btnVote!!.visibility = View.VISIBLE
        btnCreateCampaign!!.visibility = View.GONE
        btnCreate!!.visibility = View.VISIBLE
    }

    private fun adminChoice() {
        facultyContainer!!.visibility = View.VISIBLE

        btnVote!!.visibility = View.GONE
        btnCreateCampaign!!.visibility = View.VISIBLE
        btnCreate!!.visibility = View.VISIBLE
    }

    private fun checkStudentCredits(username: String, password: String) {
        var faculty: String?
        var voter: String?
        db.collection("Voters")
            .whereEqualTo("_username", username)
            .whereEqualTo("_password", password)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    Toast.makeText(this, "Wrong Credits", Toast.LENGTH_SHORT).show()
                } else {
                    faculty = it.documents[0]["_faculty"] as String
                    voter = it.documents[0]["_name"] as String
                    val intent = Intent(this, CampaignActivity::class.java)
                    intent.putExtra("FACULTY", faculty)
                    intent.putExtra("VOTER_NAME", voter)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't Find Voter", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAdminCredits(username: String, password: String, code: String) {
        var faculty: String?
        db.collection("Admins")
            .whereEqualTo("_username", username)
            .whereEqualTo("_password", password)
            .whereEqualTo("_code", code)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    Toast.makeText(this, "Wrong Credits", Toast.LENGTH_SHORT).show()
                } else {
                    faculty = it.documents[0]["_faculty"] as String
                    db.collection("Campaign").get()
                            .addOnSuccessListener { itt ->
                                if (itt.documents.isEmpty()) {
                                    val intent = Intent(this@MainActivity, CreateCampaignActivity::class.java)
                                    intent.putExtra("FACULTY", faculty)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this@MainActivity, EditCampaignActivity::class.java)
                                    intent.putExtra("FACULTY", faculty)
                                    startActivity(intent)
                                }
                            }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't Find Admin", Toast.LENGTH_SHORT).show()
            }
    }
}
