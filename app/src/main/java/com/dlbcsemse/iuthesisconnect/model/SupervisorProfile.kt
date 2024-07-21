package com.dlbcsemse.iuthesisconnect.model

class SupervisorProfile (private val svId: Int, private val svUserId: Int, private val svStatus : AvailabilityStatus, private val svBiography : String,
                         private val svTopicCategories : Array<String>, private val svResearchTopics : String, private val svLanguages : Array<String> ) {
    companion object {
        fun emptySupervisorProfile() : SupervisorProfile =
            SupervisorProfile(-1, -1, AvailabilityStatus.blocked, "",
                emptyArray(), "", emptyArray())
    }

    var id : Int = svId
    var userId : Int = svUserId
    var status : AvailabilityStatus = svStatus
    var biography : String = svBiography
    var topicCategories : Array<String> = svTopicCategories
    var researchTopics : String = svResearchTopics
    var languages : Array<String> = svLanguages

}