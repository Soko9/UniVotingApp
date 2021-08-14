package com.tutorialprojects.votingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditCampaignActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private var editDeadline: TextView? = null
    private var editName: TextView? = null
    private var editDescription: TextView? = null
    private var editCandidatesTitle: TextView? = null
    private var editCandidates: ListView? = null
    private var editEnd: androidx.appcompat.widget.AppCompatImageButton? = null

    private var arrayAdapter: ArrayAdapter<String>? = null

    private var arrayID = ArrayList<String>()

    private val scaleAnimation: ScaleAnimation = ScaleAnimation(
        1f,
        0.8f,
        1f,
        0.8f,
        50f,
        50f
    )

    var winner = ""
    var max = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_campaign)

        val faculty = intent.extras?.get("FACULTY").toString()

        connectWidgets()

        scaleAnimation.duration = 250
        scaleAnimation.setInterpolator(this, android.R.interpolator.cycle)

        var name: String? = null
        var description: String? = null
        var deadline: String? = null
        var candidates: ArrayList<String>? = null

        db.collection("Campaign")
            .whereEqualTo("_faculty", faculty)
            .get()
            .addOnSuccessListener { it ->
                if (it.size() < 1) {
                    Toast.makeText(this, "There Is No Campaigns Yet", Toast.LENGTH_SHORT).show()
                } else {
                    name = it.documents[0].get("_name") as String
                    description = it.documents[0].get("_description") as String
                    deadline = it.documents[0].get("_deadline") as String
                    candidates = it.documents[0]["_candidates"] as ArrayList<String>
                }
                editDeadline!!.text = deadline!!.toString()
                editName!!.text = name.toString()
                editDescription!!.text = description.toString()

                arrayAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    candidates!!
                )

                editCandidates!!.adapter = arrayAdapter
            }

        editEnd!!.setOnClickListener {
            editEnd!!.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    db.collection("Voters").whereEqualTo("_faculty", faculty).get()
                            .addOnSuccessListener { itt ->
                                for (i in itt.documents) {
                                    arrayID.add(i.id)
                                }

                                for (i in itt.documents) {
                                    val vts = i["_votes"] as Int
                                    if (vts > max)
                                        max = vts
                                }

                                Log.i("TAGGY", "Max = $max")

                                db.collection("Voters").whereEqualTo("_votes", max).get()
                                        .addOnSuccessListener { ittt ->
                                            winner = ittt.documents[0]["_name"] as String

                                            editDeadline!!.text = "Winner Is"
                                            editName!!.text = winner
                                            editDescription!!.text = "With $max Votes"
                                            editCandidatesTitle!!.visibility = View.GONE
                                            editCandidates!!.visibility = View.GONE
                                            editEnd!!.visibility = View.GONE

                                            for (id in arrayID) {
                                                val current = db.collection("Voters").document(id)
                                                current.update("_votes", 0)
                                                current.update("_voted", false)
                                            }

                                            db.collection("Campaign").whereEqualTo("_faculty", faculty).get()
                                                    .addOnSuccessListener {
                                                        val cid = it.documents[0].id

                                                        db.collection("Campaign").document(cid).delete()
                                                                .addOnSuccessListener {
                                                                    Log.i("TAGGY", "The Campaign Has Been Dropped!!!")
                                                                }
                                                    }
                                        }
                            }
                }
            })
        }
    }

    private fun connectWidgets() {
        editDeadline = findViewById(R.id.editDeadline)
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)
        editCandidatesTitle = findViewById(R.id.editCandidatesTitle)
        editCandidates = findViewById(R.id.editCandidates)
        editEnd = findViewById(R.id.editEnd)
    }
}