package com.dlbcsemse.iuthesisconnect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.Chat
import com.dlbcsemse.iuthesisconnect.model.ChatMessage
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.io.ByteArrayOutputStream
import java.util.Calendar

class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonSend : ImageButton
    private lateinit var buttonAttach : ImageButton
    private lateinit var editTextMessage : EditText
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var currentUser : UserProfile
    private lateinit var recipientUser : UserProfile
    private lateinit var chatHeader : Chat
    private lateinit var pickPdfLauncher: ActivityResultLauncher<Intent>
    private var attachment : ByteArray? = null
    private var attachmentName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val chatId = intent.getIntExtra("chatId", -1)

        recyclerView = findViewById(R.id.chatRecyclerViewMessageList)
        buttonSend = findViewById(R.id.chatButtonSend)
        buttonAttach = findViewById(R.id.chatButtonAttach)
        editTextMessage = findViewById(R.id.chatEditTextMessage)

        databaseHelper = DatabaseHelper(this)
        currentUser = databaseHelper.getCurrentUser()
        chatHeader = databaseHelper.getChat(chatId)
        recipientUser = currentUser
        if (chatHeader.user1.id == currentUser.id){
            recipientUser = chatHeader.user2
        }
        else{
            recipientUser = chatHeader.user1
        }

        databaseHelper.setChatReadedFlag(chatId, currentUser.id)

        adapter = ChatMessageAdapter(this)
        adapter.addAllMessages(databaseHelper.getChatMessages(chatId))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView. adapter = adapter




        buttonSend.setOnClickListener {
            if (editTextMessage.text.isNotEmpty()) {
                val messageText = editTextMessage.text.toString()
                val currentTime = Calendar.getInstance().timeInMillis.toInt()

                val message = ChatMessage(
                    -1,
                    chatId,
                    recipientUser,
                    messageText,
                    currentTime,
                    false
                )
                if (attachment != null) {
                    message.attachment = attachment
                    message.attachmentName = attachmentName
                }
                adapter.addMessage(message)
                databaseHelper.addChatMessage(chatId, recipientUser.id , messageText, currentTime, attachment, attachmentName )

                editTextMessage.text.clear()
            }
        }

        pickPdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here, e.g., get the URI of the selected file
                val data: Intent? = result.data
                val uri = data?.data
                // TODO: Do something with the URI (e.g., display or process the PDF)
                if (uri != null) {
                    attachment = getBytesFromUri(this, uri)
                    attachmentName = getFileNameFromUri(this, uri).toString()
                }
            }
        }

        buttonAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }

            pickPdfLauncher.launch(intent)
        }
    }


    fun getBytesFromUri(context: Context, uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
            byteArrayOutputStream.write(buffer, 0, len)
        }
        inputStream?.close()
        return byteArrayOutputStream.toByteArray()
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}