package com.arief.wuenak.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ActionMode
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.arief.wuenak.R
import com.arief.wuenak.sharedpreference.IntroPreference
import com.arief.wuenak.static.StaticCode.Companion.RC_SIGN_IN
import com.arief.wuenak.ultility.ProgressBarUtility
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var manager: IntroPreference

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: ActionMode.Callback2

    private val progressBar = ProgressBarUtility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        manager = IntroPreference(this)
        if(manager.isFirstRun()){
            manager.setFirstRun()
        }

        val googlebutton: Button = findViewById(R.id.login_google)
        val facebookbutton: Button = findViewById(R.id.login_facebook)

        googlebutton.setOnClickListener(this)
        facebookbutton.setOnClickListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn()


    }

    private fun configureGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_google -> {
                progressBar.show(this, "Please Wait...")

                val signInIntent: Intent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.login_facebook -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                progressBar.dialog.dismiss()
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val goMain = Intent(this@LoginActivity, MainActivity::class.java)
                progressBar.dialog.dismiss()
                startActivity(goMain)
                finish()
            } else {
                progressBar.dialog.dismiss()
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        if (user != null) {
            val goMain = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(goMain)
            finish()
        }
    }
}
