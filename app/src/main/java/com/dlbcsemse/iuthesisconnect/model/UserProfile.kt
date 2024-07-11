package com.dlbcsemse.iuthesisconnect.model

data class UserProfile (val id : Long, val name : String, val eMail : String, val type : Int)  {
    var userId : Long = id
    var userName : String = name
    var userEmail : String = eMail
    var userType : DashboardUserType = DashboardUserType.entries[type]
    lateinit var picture : String
    lateinit var biography : String
    var languages : ArrayList<Language> = ArrayList<Language>()
    var status : AvailabilityStatus = AvailabilityStatus.free


    constructor(id : Long, name : String, eMail : String, type : String)
    : this(
        id,
        name,
        eMail,
        DashboardUserType.valueOf(type).ordinal
    ){

    }

    init {
        picture = ""
        biography = ""
    }


}