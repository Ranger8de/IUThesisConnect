package com.dlbcsemse.iuthesisconnect.model
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import org.intellij.lang.annotations.Language
import java.io.Serializable
data class UserProfile (val id : Int, val name : String, val eMail : String, val type : Int)  {
    var userId : Int = id
    var userName : String = name
    var userEmail : String = eMail
    var userType : DashboardUserType = DashboardUserType.entries[type]
    var picture : String = ""

    constructor(id : Int, name : String, eMail : String, type : String)
            : this(
        id,
        name,
        eMail,
        DashboardUserType.valueOf(type).ordinal
    )
    companion object {
        fun emptyUserProfile() : UserProfile =
            UserProfile(-1, "kein Nutzer zugewiesen", "", "student")
    }
}