package com.arief.wuenak.Activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arief.wuenak.R
import com.arief.wuenak.SharedPreference.IntroPreference

class LoginActivity : AppCompatActivity() {

    private lateinit var manager: IntroPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        manager = IntroPreference(this)
        if(manager.isFirstRun()){
            manager.setFirstRun()
        }
    }

}
