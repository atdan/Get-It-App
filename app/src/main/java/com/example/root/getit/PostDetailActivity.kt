package com.example.root.getit

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.root.getit.common.Common
import com.example.root.getit.model.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PostDetailActivity : AppCompatActivity() {

    private val TAG = "PostDetailActivty"

    internal lateinit var profileImage: ImageView
    internal lateinit var myUploadsImage:ImageView
    internal lateinit var postImage:ImageView

    internal lateinit var price: TextView
    internal lateinit var title:TextView
    internal lateinit var location:TextView
    internal lateinit var description:TextView
    internal lateinit var posterName:TextView
    internal lateinit var posterPhoneNumber:TextView

    internal lateinit var posterProfileImage: CircleImageView

    internal var postId: String? = ""
    internal var categoryString: String? = ""

    internal var current_post: Post? = null

    internal lateinit var auth: FirebaseAuth
    internal lateinit var authStateListener: FirebaseAuth.AuthStateListener
    internal lateinit var database: FirebaseDatabase
    internal lateinit var postrefHome: DatabaseReference
    internal lateinit var postRefMyAds:DatabaseReference
    internal lateinit var postRefCategory:DatabaseReference

    internal var chatMessage: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)


        initFirebase()

        posterProfileImage = findViewById(R.id.profileImage)

        profileImage = findViewById(R.id.view_profile)

        myUploadsImage = findViewById(R.id.view_cart)

        postImage = findViewById(R.id.postImage)
        price = findViewById(R.id.price)
        title = findViewById(R.id.title)
        location = findViewById(R.id.location)
        description = findViewById(R.id.description)
        posterName = findViewById(R.id.name)
        posterPhoneNumber = findViewById(R.id.phoneNumber)

        posterPhoneNumber.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + posterPhoneNumber.text.toString())
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "onClick: Phone Number is null")
            }
        }


        //get food id
        if (intent != null) {
            postId = intent.getStringExtra("postid")
            categoryString = intent.getStringExtra("categoryString")
        }


        if (!postId!!.isEmpty() && postId != null) {
            if (Common.isConnectedToInternet(this)) {
                when (intent.getStringExtra(Common.FROM_ACTIVITY)) {
                    Common.HOME_ACTIVITY -> getDetailFoodHome(postId!!)
                    Common.MY_UPLOADS_ACTIVITY -> getDetailFoodMyUploads(postId!!)

                    Common.CATEGORIES_ACTIVITY -> getDetailFoodCategories(postId!!)
                }


            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                return
            }

        }
    }


    private fun getDetailFoodCategories(postid: String) {
        postRefCategory.child(this.categoryString!!).child(postid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                current_post = dataSnapshot.getValue(Post::class.java)
                price.text = if (current_post != null) current_post!!.price else "0"
                title.setText(current_post!!.title)
                location.setText(current_post!!.location)
                description.setText(current_post!!.description)
                posterName.setText(current_post!!.name)
                posterPhoneNumber.setText(current_post!!.phone)

                Picasso.with(baseContext)
                        .load(current_post!!.imageurl)
                        .into(postImage)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getDetailFoodMyUploads(postId: String) {
        Log.e("Called my ad method: ", "Start")
        postRefMyAds.child(Objects.requireNonNull<FirebaseUser>(auth.currentUser).uid).child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                current_post = dataSnapshot.getValue(Post::class.java)

                try {
                    price.text = if (current_post != null) current_post!!.price else "0"
                    title.setText(current_post!!.title)
                    location.setText(current_post!!.location)
                    description.setText(current_post!!.description)
                    posterName.setText(current_post!!.name)
                    posterPhoneNumber.setText(current_post!!.phone)

                    Picasso.with(baseContext)
                            .load(current_post!!.imageurl)
                            .into(postImage)

                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getDetailFoodHome(postId: String) {


        postrefHome.child("Raw Post").child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    current_post = dataSnapshot.getValue(Post::class.java)

                    price.text = if (current_post != null) current_post!!.price else "0"
                    title.setText(current_post!!.title)
                    location.setText(current_post!!.location)
                    description.setText(current_post!!.description)
                    posterName.setText(current_post!!.name)
                    posterPhoneNumber.setText(current_post!!.phone)

                    Picasso.with(baseContext)
                            .load(current_post!!.imageurl)
                            .into(postImage)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //check user presence
            val user = firebaseAuth.currentUser

            if (user != null) {
                finish()

            }
        }
        //init Firebase
        database = FirebaseDatabase.getInstance()
        postrefHome = database.getReference("Post Item")
        postRefMyAds = database.getReference(Common.NODE_POST_ITEM)
        postRefCategory = database.getReference("Post Item")
    }

    fun viewProfile(view: View) {
        startActivity(Intent(this@PostDetailActivity, ProfileActivity::class.java))
    }

    fun viewYourUploads(view: View) {
        startActivity(Intent(this@PostDetailActivity, MyAdsActivity::class.java))
    }
}
