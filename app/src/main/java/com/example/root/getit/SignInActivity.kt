package com.example.root.getit

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.root.getit.common.Common
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import info.hoang8f.widget.FButton
import io.paperdb.Paper

class SignInActivity : AppCompatActivity() {
    internal lateinit var email: TextInputEditText
    internal lateinit var password:TextInputEditText
    internal lateinit var signInBtn: FButton
    internal lateinit var chkRemember: com.rey.material.widget.CheckBox
    internal lateinit var txtForgotPassword: TextView
    internal lateinit var signUp:TextView

    //firebase
    internal lateinit var database: FirebaseDatabase
    internal lateinit var table_user: DatabaseReference
    internal lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        //init Firebase
        database = FirebaseDatabase.getInstance()


        //        Toolbar toolbar = findViewById(R.id.toolbar);
        //        setSupportActionBar(toolbar);
        //        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance()


        email = findViewById(R.id.edtEmail)
        password = findViewById<TextInputEditText>(R.id.edtPassword)

        signInBtn = findViewById(R.id.btn_signIn_activity)

        chkRemember = findViewById(R.id.ckbRememberMe)

        signUp = findViewById<TextView>(R.id.signUpAct)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)

        //init paper
        Paper.init(this)



        table_user = database.getReference("User")
        table_user.keepSynced(true)


        auth.addAuthStateListener {
            if (auth.currentUser == null || !auth.currentUser!!.isEmailVerified) {

            } else {

            }
        }

        txtForgotPassword.setOnClickListener { startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java)) }

        signUp.setOnClickListener(View.OnClickListener { startActivity(Intent(this@SignInActivity, SignUpActivity::class.java)) })
        signInBtn.setOnClickListener(View.OnClickListener {
            if (Common.isConnectedToInternet(baseContext)) {

                // TODO: save user and password

                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setMessage("Please Wait...")
                progressDialog.show()
                if (chkRemember.isChecked()) {
                    Paper.book().write(Common.USER_KEY, email.text!!.toString())
                    Paper.book().write<String>(Common.PASSWORD_KEY, password.getText()!!.toString())
                }

                val txtemail = email.text!!.toString()
                val txtpassword = password.getText()!!.toString()

                if (TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword)) {

                    Toasty.error(this@SignInActivity, "All fields are required", Toast.LENGTH_SHORT).show()

                } else {
                    auth.signInWithEmailAndPassword(txtemail, txtpassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    progressDialog.dismiss()
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toasty.error(this@SignInActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()

                                }
                            }
                }

                //                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                //                        @Override
                //                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //
                //                            // To check if user does not exist in db
                //                            if (dataSnapshot.child(email.getText().toString()).exists()) {
                //
                //
                //                                // Get user info
                //
                //                                progressDialog.dismiss();
                //                                User user = dataSnapshot.child(email.getText().toString()).getValue(User.class);
                //                                user.setEmail(email.getText().toString()); // set phone
                //                                if (user.getPassword().equals(password.getText().toString())) {
                //                                    {
                //                                        Toast.makeText(SignInActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                //                                        Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                //                                        Common.current_user = user;
                //                                        startActivity(homeIntent);
                //                                        finish();
                //
                //                                        table_user.removeEventListener(this);
                //                                    }
                //
                //
                //
                //                                } else {
                //                                    Toast.makeText(SignInActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                //
                //                                }
                //                            }else {
                //                                progressDialog.dismiss();
                //                                Toast.makeText(SignInActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                //
                //                            }
                //
                //                        }
                //
                //                        @Override
                //                        public void onCancelled(@NonNull DatabaseError databaseError) {
                //
                //                        }
                //                    });
            } else {
                Toasty.error(this@SignInActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
        })
    }
}
