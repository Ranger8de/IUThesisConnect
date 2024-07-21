package com.dlbcsemse.iuthesisconnect

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.DashboardButtonAdapter.ButtonViewHolder
import com.dlbcsemse.iuthesisconnect.model.Chat
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.util.Base64

class ChatAdapter (
    private val chats: List<Chat>,
    private val currentUser : UserProfile,
    private val onItemClick: (Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        val otherUser : UserProfile
        if (currentUser.id == chat.user1.userId)
            otherUser = chat.user2
        else if (currentUser.id == chat.user2.userId)
            otherUser = chat.user1
        else
            throw Exception("internal Error")

        val decodedString: ByteArray = Base64.getDecoder().decode(otherUser.picture.toByteArray())
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.imageViewProfilePicture.setImageBitmap(decodedByte)
        holder.textViewUserName.text = otherUser.name
        holder.textViewLastMessage.text = chat.getLastMessage()?.message.toString()
        holder.cardView.setOnClickListener{onItemClick(chat)}
    }

    override fun getItemCount() = chats.size


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProfilePicture : ImageView = itemView.findViewById(R.id.chatOverviewProfilePicture)
        val textViewUserName: TextView = itemView.findViewById(R.id.chatOverViewUserName)
        val textViewLastMessage : TextView = itemView.findViewById(R.id.chatOverViewLastMessage)
        val textViewLastTime : TextView = itemView.findViewById(R.id.chatOverViewLastTime)
        val cardView : CardView = itemView.findViewById(R.id.chatOverviewCardView)
    }
}