package com.example.smack.COntroller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.Model.Channel
import com.example.smack.Model.Message
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.MessageService
import com.example.smack.Services.UserDataService
import com.example.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import com.example.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    lateinit var channelAdapter: ArrayAdapter<Channel>

    var selectedChannel: Channel? = null

    private fun setUpAdapters() {

        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)

        channel_list.adapter = channelAdapter

    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val userDataChangeReciever = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {

                usernameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(
                    UserDataService.returnAvatarColor(
                        UserDataService.avatarColor
                    )
                )
                loginBtnNavHeader.text = "LogOut"
                MessageService.getChannels { complete ->

                    if (complete) {
                        if (MessageService.channels.count() > 0) {

                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }


                    }

                }
            }
        }

    }

    fun updateWithChannel() {

        mainChannelName.text = "#${selectedChannel?.name}"

        //downloadMessage for channel

        if (selectedChannel != null) {

            MessageService.getMessages(selectedChannel!!.id) {complete ->

                if (complete) {

                    for (message in MessageService.messages) {

                        println(message.message)

                    }

                }

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools,
                R.id.nav_share,
                R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        socket.connect()

        socket.on("channelCreated", onNewChannel)

        socket.on("messageCreated", onNewMesaage)

        setUpAdapters()

        channel_list.setOnItemClickListener { _, _, i, _ ->

            selectedChannel = MessageService.channels[i]
            drawerLayout.closeDrawer(GravityCompat.START)
            updateWithChannel()

        }

        if (App.prefs.isLoggedIn) {

            AuthService.findUserByEmail(this) {}

        }

    }

    override fun onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReciever, IntentFilter(
                BROADCAST_USER_DATA_CHANGED
            )
        )

        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReciever)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loginBtnNavClicked(view: View) {

        if (App.prefs.isLoggedIn) {

            UserDataService.logOut()
            usernameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

        } else {

            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)

        }

    }

    fun addChannelClicked(view: View) {

        if (App.prefs.isLoggedIn) {

            val builder = AlertDialog.Builder(this)

            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { _: DialogInterface?, _: Int ->

                    //perform logic when clicked

                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)

                    val descTextField =
                        dialogView.findViewById<EditText>(R.id.addChannelDescriptionTxt)

                    val channelName = nameTextField.text.toString()

                    val channelDescription = descTextField.text.toString()

                    // create channel with channel name and description

                    socket.emit("newChannel", channelName, channelDescription)


                }
                .setNegativeButton("Cancel") { _: DialogInterface?, _: Int ->

                    //cancel and close the dialog


                }
                .show()
        }

    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {

            runOnUiThread {

                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)

                MessageService.channels.add(newChannel)

                channelAdapter.notifyDataSetChanged()
            }

        }

    }

    private val onNewMesaage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {

            runOnUiThread {
                val channelId = args[2] as String

                if (channelId == selectedChannel?.id) {

                    val msgBody = args[0] as String

                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(
                        msgBody,
                        userName,
                        channelId,
                        userAvatar,
                        userAvatarColor,
                        id,
                        timeStamp
                    )
                    MessageService.messages.add(newMessage)


                }

            }


        }


    }

    fun sendMsgBtnClicked(view: View) {

        if (App.prefs.isLoggedIn && messageTextFIeld.text.isNotEmpty() && selectedChannel != null) {

            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit(
                "newMessage", messageTextFIeld.text.toString(), userId, channelId,
                UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor
            )

            messageTextFIeld.text.clear()
            hideKeyBoard()
        }


    }

    fun hideKeyBoard() {

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {

            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)

        }

    }
}
