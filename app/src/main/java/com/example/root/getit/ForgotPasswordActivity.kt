package com.example.root.getit

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ForgotPasswordActivity : AppCompatActivity() {

    internal lateinit var send_email: EditText
    internal lateinit var btn_reset: Button
    internal lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle("Reset Password")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        send_email = findViewById(R.id.send_email)
        btn_reset = findViewById(R.id.btn_reset)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_reset.setOnClickListener {
            val email = send_email.text.toString()

            if (email == "") {
                Toast.makeText(this@ForgotPasswordActivity, "All fields are required", Toast.LENGTH_SHORT).show()

            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Password sent to email", Toast.LENGTH_SHORT).show()
                        if (firebaseAuth.currentUser == null) {
                            startActivity(Intent(this@ForgotPasswordActivity, SignInActivity::class.java))
                        } else {

                            val alertDialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                            alertDialog.setTitle("Log out")
                            alertDialog.setMessage("You will be logged out, continue?")
                            alertDialog.setIcon(R.drawable.ic_security_black_24dp)
                            alertDialog.show()

                            alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                                firebaseAuth.signOut()
                                startActivity(Intent(this@ForgotPasswordActivity, SignInActivity::class.java))
                                dialogInterface.dismiss()
                            }.setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }


                        }

                    } else {

                        val error = task.exception!!.message
                        Toast.makeText(this@ForgotPasswordActivity, error, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }
}
