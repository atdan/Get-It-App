package com.example.root.getit

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.root.getit.common.Common
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import info.hoang8f.widget.FButton
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    internal lateinit var btnSignUp: FButton
    internal lateinit var btnSignIn:FButton

    internal lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        btnSignIn = findViewById(R.id.btn_signIn)
        btnSignUp = findViewById(R.id.btn_signUp)

        auth = FirebaseAuth.getInstance()

        //init paper
        Paper.init(this)

        btnSignIn.setOnClickListener {
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        //check user
        val user = Paper.book().read<String>(Common.USER_KEY)
        val password = Paper.book().read<String>(Common.PASSWORD_KEY)
        if (user != null && password != null) {
            if (!user!!.isEmpty() && !password!!.isEmpty())
                login(user, password)
        }
    }

    private fun login(email: String, password: String) {


        if (Common.isConnectedToInternet(baseContext)) {

            val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Please Wait...")
            progressDialog.show()

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            progressDialog.dismiss()
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()

                        }
                    }
        } else {
            Toast.makeText(this@MainActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show()
            return
        }

    }
}
