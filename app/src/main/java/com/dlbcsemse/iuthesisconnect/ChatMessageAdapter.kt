package com.dlbcsemse.iuthesisconnect

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.ChatMessage
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Locale


class ChatMessageAdapter (val context : Context) : RecyclerView.Adapter<MessageViewHolder>(){

    private val messages: ArrayList<ChatMessage> = ArrayList()
    private lateinit var currentUser : UserProfile

    init {
        val databaseHelper = DatabaseHelper(this.context)
        currentUser = databaseHelper.getCurrentUser()
    }

    fun addMessage(message: ChatMessage){
        messages.add(message)
        notifyDataSetChanged()
    }

    fun addAllMessages(messageList : List<ChatMessage>){
        messages.addAll(messageList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position)

        return if(currentUser.id == message.toUser.id) {
            MessageType.Other.ordinal
        }
        else {
            MessageType.My.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType == MessageType.My.ordinal) {
            MessageViewHolderMy(
                LayoutInflater.from(context).inflate(R.layout.message_my, parent, false)
            )
        } else {
            MessageViewHolderOther(
                LayoutInflater.from(context).inflate(R.layout.message_other, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)

        holder?.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolderMy (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.chatTextViewMessageMy)
        private var timeText: TextView = view.findViewById(R.id.chatTextViewMessageTimeMy)
        private var imgAttachment : ImageView = view.findViewById(R.id.chatImageViewAttachmentMy)
        private var textAttachment : TextView = view.findViewById(R.id.chatTextViewAttachementNameMy)

        override fun bind(message: ChatMessage) {
            messageText.text = message.message
            timeText.text = fromMillisToTimeString(message.time)
            if (message.attachment != null) {
                if (message.attachment!!.isNotEmpty()) {
                    imgAttachment.visibility = View.VISIBLE
                    textAttachment.visibility = View.VISIBLE
                    textAttachment.text = message.attachmentName
                }
            }
        }
    }

    inner class MessageViewHolderOther (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.chatTextViewMessageOther)
        private var userText: TextView = view.findViewById(R.id.chatTextViewUserOther)
        private var timeText: TextView = view.findViewById(R.id.chatTextViewMessageTimeOther)
        private var imgAttachment : ImageView = view.findViewById(R.id.chatImageViewAttachmentOther)
        private var textAttachment : TextView = view.findViewById(R.id.chatTextViewAttachementNameOther)

        override fun bind(message: ChatMessage) {
            messageText.text = message.message
            userText.text = message.toUser.name
            timeText.text = fromMillisToTimeString(message.time)
            if (message.attachment != null) {
                if (message.attachment!!.isNotEmpty()) {
                    imgAttachment.visibility = View.VISIBLE
                    textAttachment.visibility = View.VISIBLE
                    textAttachment.text = message.attachmentName
                }
            }
        }
    }


    fun fromMillisToTimeString(millis: Int) : String {
        val format = SimpleDateFormat("hh:mm", Locale.getDefault())
        return format.format(millis)
    }

}


open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: ChatMessage) {
    }
}