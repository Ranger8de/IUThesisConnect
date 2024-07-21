package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.Chat
import com.dlbcsemse.iuthesisconnect.model.ChatMessage
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class ChatOverviewActivity : AppCompatActivity() {
    private lateinit var recycler : RecyclerView
    private val dbHelper = DatabaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentUser = dbHelper.getCurrentUser()

        recycler = findViewById(R.id.chatOverviewRecyclerViewChatList)
        recycler.layoutManager = LinearLayoutManager(this)
        val chats: List<Chat> = dbHelper.getChats(currentUser.id.toInt())
        var chatAdapter = ChatAdapter(chats, currentUser ) {
            clickedItem ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", clickedItem.id)
            startActivity(intent)
        }
        recycler.adapter = chatAdapter
    }

    private fun createExampleChats(): ArrayList<Chat> {

        val chats = ArrayList<Chat>()
        val user1 = dbHelper.getUser("student")
        val user2 = dbHelper.getUser("supervisor")

        if (user1 != null && user2 != null) {
            val chat = Chat(1, user1, user2)

            val message1 = ChatMessage(-1, 1, user1, "Hello", 1, false)
            val message2 = ChatMessage(-1, 1, user2, "Hello back", 1, false)

            chat.addMessage(message1)
            chat.addMessage(message2)

            chats.add(chat)
        }
        return chats
    }
}