package com.dlbcsemse.iuthesisconnect.model

data class ChatMessage (private val ID : Int, private val chatID : Int, private val toUserProfile: UserProfile,
                        private val Message : String, private val SendTime : Int , private val isReaded : Boolean) {
    var id = ID
    var chatId = chatID
    var toUser = toUserProfile
    var message = Message
    var time = SendTime
    var readed = isReaded

    var attachment : ByteArray? = null
    var attachmentName : String? = null

}