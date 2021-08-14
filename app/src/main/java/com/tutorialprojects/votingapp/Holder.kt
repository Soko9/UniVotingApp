package com.tutorialprojects.votingapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Holder(view: View): RecyclerView.ViewHolder(view) {
    private val db = Firebase.firestore

    private val candidateImage: androidx.appcompat.widget.AppCompatImageButton = itemView.findViewById(R.id.candidateImage)
    private val candidateName: TextView = itemView.findViewById(R.id.candidateName)
    private val candidateDepartment: TextView = itemView.findViewById(R.id.candidateDepartment)
    private val candidateCountry: TextView = itemView.findViewById(R.id.candidateCountry)
    private val candidateDescription: TextView = itemView.findViewById(R.id.candidateDescription)
    private val btnVoting: androidx.appcompat.widget.AppCompatImageButton = itemView.findViewById(R.id.btnVoting)

    private var isVoted: Boolean? = null
    private var vid: String? = null
    private var votes: Number? = null
    private var cid: String? = null

    fun bind(candidate: Candidate, voter: String, context: Context) {
        if (candidate._gender == "Male")
            candidateImage.setImageResource(R.drawable.male)
        else if (candidate._gender == "Female")
            candidateImage.setImageResource(R.drawable.female)
        candidateName.text = candidate._name.toString()
        candidateDepartment.text = candidate._department.toString()
        candidateCountry.text = candidate._country.toString()
        candidateDescription.text = candidate._description.toString()

        db.collection("Voters")
                .whereEqualTo("_name", voter)
                .get()
                .addOnSuccessListener { it ->
                    isVoted = it.documents[0]["_voted"] as Boolean

                    Log.i("TAGGY", "Is voted: $isVoted")
                    if (isVoted == true) {
                        btnVoting.setImageResource(R.drawable.done)
                        btnVoting.setBackgroundResource(R.drawable.redone_button_background)
                        btnVoting.isEnabled = false
                    }

                    db.collection("Voters").whereEqualTo("_name", voter).get()
                            .addOnSuccessListener { itt ->
                                vid = itt.documents[0].id
                                Log.i("TAGGY", "--$vid--$voter")
                            }
                            .addOnFailureListener {
                                Log.i("TAGGY", "Failed In '2'")
                            }
                }
                .addOnFailureListener {
                    Log.i("TAGGY", "Failed In '1'")
                }

        btnVoting.setOnClickListener {
            Log.i("TAGGY", "$voter -- ${candidate._name}")
            db.collection("Voters").whereEqualTo("_name", candidate._name).get()
                    .addOnSuccessListener {
                        cid = it.documents[0].id
                        votes = it.documents[0]["_votes"] as Number?

                        db.collection("Voters").document(cid!!).update("_votes", votes!!.toInt() + 1)
                        db.collection("Voters").document(vid!!).update("_voted", true)
                                .addOnSuccessListener {
                                    btnVoting.setImageResource(R.drawable.done)
                                    btnVoting.setBackgroundResource(R.drawable.redone_button_background)
                                    btnVoting.isEnabled = false
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                }
                    }
                    .addOnFailureListener {
                        Log.i("TAGGY", "Failed In '3'")
                    }
        }
    }
}