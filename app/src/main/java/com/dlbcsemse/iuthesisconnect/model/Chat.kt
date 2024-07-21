package com.dlbcsemse.iuthesisconnect.model

data class Chat(val ID : Int, val userOne : UserProfile, val userTwo: UserProfile) {
    val id = ID
    val user1 = userOne
    val user2 = userTwo
    var messages = mutableListOf<ChatMessage>()



    fun addMessage(message: ChatMessage) {
        messages.add(message)
        // TODO: Call Firebase FCM to send push message to receiver
    }

    fun getLastMessage(): ChatMessage? {
        return if (messages.isNotEmpty()) {
            messages.last()
        } else {
            null
        }
    }
}