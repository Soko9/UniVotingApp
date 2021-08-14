package com.tutorialprojects.votingapp

class Candidate {
    var _name: String? = null
    var _department: String? = null
    var _country: String? = null
    var _description: String? = null
    var _gender: String? = null
    var _voted: Boolean? = null

    override fun toString(): String {
        return "$_name - $_department - $_gender - $_country"
    }
}