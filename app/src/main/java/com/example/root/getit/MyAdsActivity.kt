package com.example.root.getit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.root.getit.common.Common
import com.example.root.getit.interfaces.ItemClickListener
import com.example.root.getit.model.Post
import com.example.root.getit.view_holder.MyAdsViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MyAdsActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    private val TAG = "MyAdsActivity"
    internal lateinit var auth: FirebaseAuth
    internal lateinit var authStateListener: FirebaseAuth.AuthStateListener
    internal lateinit var database: FirebaseDatabase
    internal lateinit var myAds: DatabaseReference
    internal lateinit var categoryAds:DatabaseReference
    internal lateinit var homeAds:DatabaseReference

    internal lateinit var recyclerView: RecyclerView

    internal lateinit var layoutManager: RecyclerView.LayoutManager

    internal lateinit var adapter: FirebaseRecyclerAdapter<Post, MyAdsViewHolder>

    internal lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)
        toolbar = findViewById(R.id.toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar!!.setNavigationOnClickListener {
            val intent = Intent(this@MyAdsActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        init()

        initFirebase()

        loadData()
    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerview_menu)
        recyclerView.setHasFixedSize(true)
        layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        profile = findViewById(R.id.view_profile)


    }

    private fun loadData() {
        val myQuery = homeAds.orderByChild("userid").equalTo(auth.currentUser!!.uid)

        val options = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(myQuery, Post::class.java)
                .build()

        adapter = object : FirebaseRecyclerAdapter<Post, MyAdsViewHolder>(options) {
            override fun onBindViewHolder(holder: MyAdsViewHolder, position: Int, model: Post) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.image_processing)
                requestOptions.error(R.drawable.no_image)

                Glide.with(baseContext)
                        .applyDefaultRequestOptions(requestOptions)
                        .setDefaultRequestOptions(requestOptions)
                        .load(model.imageurl)
                        .into(holder.imageView)

                Log.e(TAG, "onBindViewHolder: " + model.imageurl)

                holder.txtMenuName.setText(model.title)
                holder.price.setText(model.price)
                holder.location.setText(model.location)

                holder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {

                        val intent = Intent(this@MyAdsActivity, PostDetailActivity::class.java)
                        intent.putExtra("postid", adapter.getRef(position).key)
                        intent.putExtra(Common.FROM_ACTIVITY, Common.MY_UPLOADS_ACTIVITY)
                        startActivity(intent)
                    }
                })
            }


            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyAdsViewHolder {
                val itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.menu_item_my_ads, viewGroup, false)

                return MyAdsViewHolder(itemView)
            }
        }


        adapter.startListening()
        recyclerView.adapter = adapter
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
        database = FirebaseDatabase.getInstance()
        myAds = database.getReference(Common.NODE_POST_ITEM).child(Objects.requireNonNull<FirebaseUser>(auth.currentUser).uid)

        categoryAds = database.getReference("Post Item")

        homeAds = database.getReference(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST)
    }

    //code when the food list is long clicked
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title == Common.UPDATE) {

            //showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        } else if (item.title == Common.DELETE) {

            startActivity(Intent(this@MyAdsActivity, HomeActivity::class.java))
            deleteFood(adapter.getRef(item.order).key)

        }
        return super.onContextItemSelected(item)
    }

    private fun deleteFood(key: String?) {
        myAds.child(key!!).removeValue()
        homeAds.child(key).removeValue()
    }

    fun viewProfile(view: View) {

        startActivity(Intent(this@MyAdsActivity, ProfileActivity::class.java))
    }
}
