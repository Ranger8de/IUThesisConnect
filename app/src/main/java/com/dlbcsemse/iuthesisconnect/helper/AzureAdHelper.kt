package com.dlbcsemse.iuthesisconnect.helper

import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.util.UUID

/**
 * This class simulates the communication with Azure AD
 * >> this cannot be implemented due to a lack of authorization
 */
class AzureAdHelper {

    /**
     * returns a UUID if the login was successful, otherwise an empty UUID
     */
    fun logIn(userName: String, password: String) : UUID {
        val uuid: UUID = if (userName.equals("student", true)){
            UUID.fromString("176130ce-8933-473f-914d-62b6c1e45b60")
        } else if (userName.equals("supervisor", true)){
            UUID.fromString("8c31ee19-ae5a-4b81-8da5-19c96e2fb18b")
        } else {
            UUID(0,0)
        }

        return uuid
    }

    /**
     * gets the user profile of the given name
     */
    fun getUserProfile(userName: String ) : UserProfile{
        lateinit var profile : UserProfile
        if (userName.equals("student", true)){
            profile = UserProfile(1, "student", "student@iu.org", "student")
        }
        else if (userName.equals("supervisor", true)){
            profile = UserProfile(2, "supervisor", "supervisor@iu.org", "supervisor")
        }

        return profile
    }
}