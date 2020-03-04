package com.arief.wuenak.UserInterface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arief.wuenak.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val toLogin = Intent(this, LoginActivity::class.java)
        startActivity(toLogin)
    }
}
