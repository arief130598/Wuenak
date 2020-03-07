package com.arief.wuenak.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.arief.wuenak.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.hello)
        button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.hello -> {
                FirebaseAuth.getInstance().signOut()
            }
        }
    }


}
