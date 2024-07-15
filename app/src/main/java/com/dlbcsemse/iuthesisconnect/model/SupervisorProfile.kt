package com.dlbcsemse.iuthesisconnect.model

class SupervisorProfile (val svId: Int, val svUserId: Int, val svStatus : AvailabilityStatus, val svBiography : String,
                         val svTopicCategories : Array<String>, val svResearchTopics : String ) {

    var id : Int = svId
    var userId : Int = svUserId
    var status : AvailabilityStatus = svStatus
    var biography : String = svBiography
    var topicCategories : Array<String> = svTopicCategories
    var researchTopics : String = svResearchTopics



}