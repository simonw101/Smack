package com.example.smack.COntroller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvater = "profileDefault"
    var avaterColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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

        AuthService.registerUser(this, "simonhanwilson@icloud.com", "pass123") {complete ->

            if (complete) {

                

            }

        }

    }
}
