package com.example.root.getit

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    internal lateinit var username: android.widget.EditText
    internal lateinit var email: android.widget.EditText
    internal lateinit var phone: android.widget.EditText
    internal lateinit var pasword: android.widget.EditText
    internal lateinit var register_btn: Button

    internal lateinit var auth: FirebaseAuth
    internal lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle("Register")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        username = findViewById(R.id.name)
        email = findViewById(R.id.email)
        pasword = findViewById(R.id.password)
        phone = findViewById(R.id.phone)
        register_btn = findViewById(R.id.register_btn)

        auth = FirebaseAuth.getInstance()

        signin.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        })
        register_btn.setOnClickListener {
            val txtUsername = username.text.toString()
            val txtemail = email.text.toString()
            val txtPhone = phone.text.toString()
            val txtpassword = pasword.text.toString()

            if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtemail) ||
                    TextUtils.isEmpty(txtpassword)) {
                Toast.makeText(this@SignUpActivity, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else if (txtpassword.length < 6) {
                Toast.makeText(this@SignUpActivity, "password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            } else {
                register(txtUsername, txtemail, txtPhone, txtpassword)
            }
        }
    }

    private fun register(username: String, email: String, phone: String, pasword: String) {

        val progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setTitle("Creating user")
        progressDialog.setMessage("Please wait...")
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, pasword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser!!.uid

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                        //databaseReference.keepSynced(true)

                        val hashMap = HashMap<String, String?>()
                        hashMap["id"] = userId
                        hashMap["name"] = username
                        hashMap["phone"] = phone
                        hashMap["imageUrl"] = null
                        hashMap["status"] = "offline"
                        hashMap["password"] = pasword



                        databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@SignUpActivity, "You can't register with this email", Toast.LENGTH_SHORT).show()

                                progressDialog.dismiss()
                            }
                        }

                    }
                }
    }
}
