package com.example.messenger.messeges

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messenger.LoginActivity
import com.example.messenger.NewMessageActivity
import com.example.messenger.R
import com.example.messenger.SettingsActivity
import com.example.messenger.models.ChatFromItem
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.ChatToItem
import com.example.messenger.models.User
import com.example.messenger.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_land_chat_log.*

class LandChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
        var currentUser: User? = null
    }
    val adapter1 = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_land_chat_log)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            val intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
        }


        landscape_latest_message.adapter = adapter
        landscape_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        listenForLatestMessages()
        listenForLatestMessages1()

        fetchCurrentUser()

        verifyUserIsLoggenIn()

        adapter.setOnItemClickListener{item, view->
            val row = item as LandChatLogActivity
            toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
            supportActionBar?.title = toUser?.username
            //запуск отображения сообщений
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, previousChildName: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildMoved(p0: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    val adapter = GroupAdapter<ViewHolder>()
    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun listenForLatestMessages1(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LandChatLogActivity.currentUser ?: return
                        adapter1.add(ChatToItem(chatMessage.text, currentUser, chatMessage.timestamp))
                    }
                    else{
                        adapter1.add(ChatFromItem(chatMessage.text, toUser!!, chatMessage.timestamp))
                    }
                }
                land_recyclerview_chat_log.scrollToPosition(adapter1.itemCount -1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun performSendMessage(){
        val text = land_edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        if (fromId == null) return
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis())

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                land_edittext_chat_log.text.clear()
                land_recyclerview_chat_log.scrollToPosition(adapter1.itemCount -1)
            }
        toReference.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
    val latestMessageMap = HashMap<String, ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun verifyUserIsLoggenIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}