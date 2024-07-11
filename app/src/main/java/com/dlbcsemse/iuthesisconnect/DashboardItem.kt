package com.dlbcsemse.iuthesisconnect

import com.dlbcsemse.iuthesisconnect.model.DashboardUserType

data class DashboardItem (val ID : Long, val Name : String, val ImageID : Int, val Type : DashboardUserType){
    var itemID : Long = ID
    var itemName : String = Name
    var itemImageID : Int = ImageID
    var userType : DashboardUserType = Type
}