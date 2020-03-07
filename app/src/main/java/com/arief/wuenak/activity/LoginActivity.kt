package com.arief.wuenak.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.arief.wuenak.R
import com.arief.wuenak.sharedpreference.IntroPreference
import com.arief.wuenak.static.StaticCode.Companion.RC_SIGN_IN
import com.arief.wuenak.ultility.ProgressBarUtility
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var manager: IntroPreference

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private val callbackManager = CallbackManager.Factory.create();

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
        configureFacebookSignIn(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_google -> {
                progressBar.show(this, "Please Wait...")

                val signInIntent: Intent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.login_facebook -> {
                progressBar.show(this, "Please Wait...")

                LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"));
            }
        }
    }

    private fun configureFacebookSignIn(context: Context) {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    progressBar.dialog.dismiss()
                    Toast.makeText(context, "Sign In Canceled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    progressBar.dialog.dismiss()
                    Toast.makeText(context, "Sign In Failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressBar.dialog.dismiss()

                    goMain()
                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.dialog.dismiss()
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun configureGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                progressBar.dialog.dismiss()
                goMain()
                finish()
            } else {
                progressBar.dialog.dismiss()
                try {
                    throw it.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                }
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

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun goMain(){
        val goMain = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(goMain)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        if (user != null) {
            goMain()
        }
    }
}
