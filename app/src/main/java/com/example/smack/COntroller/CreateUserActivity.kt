package com.example.smack.COntroller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.UserDataService
import com.example.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvater = "profileDefault"
    var avaterColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE

    }

    fun generateUserAvatar(view: View) {

        val random = Random()

        val color = random.nextInt(2)

        val avater = random.nextInt(28)

        if (color == 0) {

            userAvater = "light$avater"

        } else {

            userAvater = "dark$avater"

        }

        val resourceId = resources.getIdentifier(userAvater, "drawable", packageName)

        createAvatarImageView.setImageResource(resourceId)

    }

    fun generateBackGroundColorClicked(view: View) {

        val random = Random()

        val r = random.nextInt(255)

        val g = random.nextInt(255)

        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255

        val savedG = g.toDouble() / 255

        val savedB = b.toDouble() / 255

        avaterColor = "[$savedR, $savedG, $savedB, 1]"

        println(avaterColor)


    }

    fun createUserClicked(view: View) {

        enableSpinner(true)

        val userName = createUserNameText.text.toString()

        val email = createEmailText.text.toString()

        val password = createPasswordText.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.registerUser(email, password) { registerSuccess ->

                if (registerSuccess) {

                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {

                            AuthService.createUser(
                                userName,
                                email,
                                userAvater,
                                avaterColor
                            ) { createSuccess ->

                                if (createSuccess) {

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGED)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()

                                } else {

                                    errorToast()

                                }

                            }

                        } else {

                            errorToast()

                        }
                    }

                } else {

                    errorToast()

                }

            }


        } else {

            Toast.makeText(
                applicationContext,
                "Make sure username, email, and password are filled in",
                Toast.LENGTH_LONG
            ).show()

            enableSpinner(false)

        }

    }

    fun errorToast() {

        Toast.makeText(
            applicationContext,
            "Something went wrong, Please try again",
            Toast.LENGTH_LONG
        ).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {

        if (enable) {

            createSpinner.visibility = View.VISIBLE

        } else {

            createSpinner.visibility = View.INVISIBLE

        }

        createUserButton.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        backGroundColorBtn.isEnabled = !enable

    }
}
