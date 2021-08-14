package com.tutorialprojects.votingapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.*

class CreateCampaignActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private val listItems = ArrayList<String>()
    private val candidates = ArrayList<String>()

    var txtCampName: AppCompatEditText? = null
    var txtCampDescription: AppCompatEditText? = null
    var txtCampTime: DatePicker? = null
    var lblCandidates: ListView? = null
    var btnDone: AppCompatImageButton? = null
    var btnCreateCamp: AppCompatImageButton? = null

    private var arrayAdapter: ArrayAdapter<String>? = null

    private val scaleAnimation: ScaleAnimation = ScaleAnimation(
            1f,
            0.8f,
            1f,
            0.8f,
            50f,
            50f
    )

    var cname: String? = null
    var cdescription: String? = null
    var ctime: String? = null

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        val faculty = intent.extras?.get("FACULTY").toString()

        connectWidgets()

        scaleAnimation.duration = 250
        scaleAnimation.setInterpolator(this, android.R.interpolator.cycle)

        val today = Calendar.getInstance()
        txtCampTime!!.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)

        ) { _, year, month, day ->
            ctime = "$day - ${month+1} - $year"
        }

        db.collection("Voters")
                .whereEqualTo("_faculty", faculty)
                .get()
                .addOnSuccessListener {
                    var i = 0
                    while (i < it.size()) {
                        listItems.add(it.documents[i]["_name"] as String)
                        i++
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }

        arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_multiple_choice,
            listItems
        )

        lblCandidates!!.adapter = arrayAdapter

        lblCandidates!!.setOnItemClickListener { _, _, i, _ ->
            if (lblCandidates!!.isItemChecked(i)) {
                if (!candidates.contains(lblCandidates!!.getItemAtPosition(i))) {
                    candidates.add(lblCandidates!!.getItemAtPosition(i) as String)
                }
            } else {
                candidates.remove(lblCandidates!!.getItemAtPosition(i))
            }
        }

        btnDone!!.setOnClickListener {
            if (candidates.isEmpty()) {
                Toast.makeText(this, "There Is No Candidates Selected...", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Candidates Are: $candidates", Toast.LENGTH_LONG).show()
            }
        }

        btnCreateCamp!!.setOnClickListener {
            btnCreateCamp!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    cname = txtCampName!!.text.toString().trim()
                    cdescription = txtCampDescription!!.text.toString().trim()

                    if (cname!!.isEmpty() || cdescription!!.isEmpty() || candidates.isEmpty()) {
                        Toast.makeText(this@CreateCampaignActivity, "You Can't Leave Anything Empty", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val campaign = hashMapOf(
                        "_name" to cname,
                        "_description" to cdescription,
                        "_deadline" to ctime,
                        "_candidates" to candidates,
                        "_faculty" to faculty
                    )

                    db.collection("Campaign")
                        .add(campaign)
                        .addOnSuccessListener {
                            Toast.makeText(this@CreateCampaignActivity, "New Campaign Has Been Created", Toast.LENGTH_SHORT).show()
                            val intent: Intent = Intent(this@CreateCampaignActivity, CampaignActivity::class.java)
                            intent.putExtra("FACULTY", faculty)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@CreateCampaignActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        }
                }
            })
        }
    }

    private fun connectWidgets() {
        txtCampName = findViewById(R.id.txtCampName)
        txtCampDescription = findViewById(R.id.txtCampDescription)
        txtCampTime = findViewById(R.id.txtCampTime)
        lblCandidates = findViewById(R.id.lblCandidates)
        btnDone = findViewById(R.id.btnDone)
        btnCreateCamp = findViewById(R.id.btnCreateCamp)
    }
}