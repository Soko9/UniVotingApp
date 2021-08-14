package com.tutorialprojects.votingapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CandidateAdapter(
        private val context: Context,
        private val ccandidates: ArrayList<Candidate>,
        private val voter: String
): RecyclerView.Adapter<Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.candidates_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current: Candidate = ccandidates[position]
        holder.bind(current, voter, context)
    }

    override fun getItemCount(): Int = ccandidates.size
}