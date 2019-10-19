package com.example.smack.COntroller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.UserDataService
import com.example.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val userDataChangeReciever = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {

                usernameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "LogOut"
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

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReciever, IntentFilter(
            BROADCAST_USER_DATA_CHANGED))

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loginBtnNavClicked(view: View) {

        if (AuthService.isLoggedIn) {

            UserDataService.logOut()
            usernameNavHeader.text = "Login"
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



    }

    fun sendMsgBtnClicked(view: View) {

    }
}
