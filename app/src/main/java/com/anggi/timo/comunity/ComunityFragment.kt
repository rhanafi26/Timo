package com.anggi.timo.comunitypackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggi.timo.Modelpackage.Message
import com.anggi.timo.R
import com.anggi.timo.adapter.ChatAdapter
import com.anggi.timo.utils.UserData
import com.anggi.timo.utils.UserManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommunityFragment : Fragment() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUser: UserData
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference("community_chat")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comunity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUser = UserManager.initUser(requireContext())

        rvChat = view.findViewById(R.id.rvChat)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        setupRecyclerView()
        listenForMessages()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(currentUser.id)

        rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true // Pesan terbaru di bawah
            }
            adapter = chatAdapter
        }

        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        })
    }

    private fun listenForMessages() {
        chatRef.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()

                for (childSnapshot in snapshot.children) {
                    val message = Message(
                        id = childSnapshot.key ?: "",
                        senderId = childSnapshot.child("senderId").getValue(String::class.java) ?: "",
                        senderName = childSnapshot.child("senderName").getValue(String::class.java) ?: "Unknown",
                        senderColor = childSnapshot.child("senderColor").getValue(String::class.java) ?: "#FF5722",
                        text = childSnapshot.child("text").getValue(String::class.java) ?: "",
                        timestamp = childSnapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                    )
                    messages.add(message)
                }

                messages.sortBy { it.timestamp }
                chatAdapter.submitList(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memuat pesan: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }
    }

    private fun sendMessage(text: String) {
        val messageId = chatRef.push().key ?: return

        val messageData = mapOf(
            "senderId" to currentUser.id,
            "senderName" to currentUser.name,
            "senderColor" to currentUser.color,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        chatRef.child(messageId).setValue(messageData)
            .addOnSuccessListener {
                etMessage.text?.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Gagal mengirim: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}