package com.tutorialprojects.votingapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.reflect.typeOf


class CampaignActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    var lblCampaignDeadline: TextView? = null
    var lblCampaignName: TextView? = null
    var lblCampaignDescription: TextView? = null
    var lblCampaignCandidates: RecyclerView? = null

    private var cadapter: CandidateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign)

        connectWidgets()

        val faculty = intent.extras?.get("FACULTY").toString()
        val voter = intent.extras?.get("VOTER_NAME").toString()

        var name: String? = null
        var description: String? = null
        var deadline: String? = null
        var candidates: ArrayList<String>? = null

        val ccandidates = ArrayList<Candidate>()

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
                lblCampaignDeadline!!.text = deadline!!.toString()
                lblCampaignName!!.text = name.toString()
                lblCampaignDescription!!.text = description.toString()

                for (candidate in candidates!!) {
                    Log.i("TAGGY", candidate)
                    val current = Candidate()
                    db.collection("Voters")
                            .whereEqualTo("_name", candidate)
                            .get()
                            .addOnSuccessListener { itt ->
                                current._name = itt.documents[0]["_name"] as String
                                current._department = itt.documents[0]["_department"] as String
                                current._country = itt.documents[0]["_country"] as String
                                current._description = itt.documents[0]["_description"] as String
                                current._gender = itt.documents[0]["_gender"] as String
                                current._voted = itt.documents[0]["_voted"] as Boolean
                                ccandidates.add(current)

                                cadapter = CandidateAdapter(applicationContext, ccandidates, voter)
                                lblCampaignCandidates!!.adapter = cadapter
                                lblCampaignCandidates!!.layoutManager = LinearLayoutManager(this)
                            }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "There Is No Campaigns Yet", Toast.LENGTH_SHORT).show()
            }
    }

    private fun connectWidgets() {
        lblCampaignDeadline = findViewById(R.id.lblCampaignDeadline)
        lblCampaignName = findViewById(R.id.lblCampaignName)
        lblCampaignDescription = findViewById(R.id.lblCampaignDescription)
        lblCampaignCandidates = findViewById(R.id.lblCampaignCandidates)
    }

}