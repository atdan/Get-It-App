package com.example.root.getit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.root.getit.common.Common
import com.example.root.getit.interfaces.ItemClickListener
import com.example.root.getit.model.Post
import com.example.root.getit.model.User
import com.example.root.getit.networksync.CheckInternetConnection
import com.example.root.getit.user_session.UserSession
import com.example.root.getit.view_holder.HomeViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var toolbar: Toolbar? = null

    private val TAG = "HomeActivity"
    internal  var database: FirebaseDatabase? = null
    internal  var category: DatabaseReference? = null

    internal  var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    internal  var recyclerView_menu: RecyclerView? = null

    internal var layoutManager: RecyclerView.LayoutManager? = null

    internal var adapter: FirebaseRecyclerAdapter<Post, HomeViewHolder>? = null


    internal  var auth: FirebaseAuth? = null

    internal var authStateListener: FirebaseAuth.AuthStateListener? = null
    internal  var userDatabase: DatabaseReference? = null
    internal var myAds: DatabaseReference? = null


    internal var user: User? = null
    internal var nameS: String? = null
    internal var mobileS:String? = null
    internal var photoUrlS:String? = null
    internal var addressS:String? = null
    internal lateinit var userDrawerImage: CircleImageView
    internal lateinit var profileAc: ImageView
    internal lateinit var cartAc:ImageView
    internal lateinit var userDrawerName: TextView
    internal lateinit var userDrawerEmail:TextView
    internal lateinit var swipeRefreshLayout: SwipeRefreshLayout
    internal lateinit var searchBar: MaterialSearchBar

    //Searchbar functionality
    internal var searchAdapter: FirebaseRecyclerAdapter<Post, HomeViewHolder>? = null
    internal var suggestList: MutableList<String> = ArrayList()
    private var sesion: UserSession? = null

    internal lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //check Internet Connection
        CheckInternetConnection(this).checkConnection()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val headerView = nav_view.getHeaderView(0)
        userDrawerImage = headerView.findViewById(R.id.userDrawerImageView)

        userDrawerEmail = headerView.findViewById(R.id.userDrawerEmail)

        userDrawerName = headerView.findViewById(R.id.userDrawerName)


        sesion = UserSession(this)

        if (sesion!!.firstTime!!) {
            //tap target view
            tapview()
            sesion!!.firstTime = (false)
        }

        init()
        verifyUserIsLoggedIn()
        if (verifyUserIsLoggedIn()){
            loadList()
            loadUserDrawer()
            loadSuggest()
        }

//        loadData();



    }

    private fun init() {

        //init Firebase

        auth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //check user presence
            val user = firebaseAuth.currentUser

            if (user != null) {
                finish()
                if (auth != null) {
                    userDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(auth!!.currentUser!!.uid)
                    myAds = database!!.reference.child(Common.NODE_POST_ITEM)
                            .child(auth!!.currentUser!!.uid)
                }

                //                    Intent moveToHome = new Intent(HomeActivity.this, HomeActivity.class);
                //                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                    startActivity(moveToHome);
            }
        }
        database = FirebaseDatabase.getInstance()
        category = database!!.reference.child(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST)






        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        collapsingToolbarLayout = findViewById(R.id.collapsing)

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar)


        //init VIews

        searchBar = findViewById(R.id.searchBar)
        recyclerView_menu = findViewById(R.id.recyclerview_menu)
        recyclerView_menu!!.setHasFixedSize(true)
        layoutManager = GridLayoutManager(this, 2)
        recyclerView_menu!!.layoutManager = layoutManager

        profileAc = findViewById(R.id.view_profile)
        cartAc = findViewById(R.id.view_cart)


        initSwipeLayout()

        //initSearchBar()


        profileAc.setOnClickListener { view -> viewProfile(view) }

        cartAc.setOnClickListener {
            //viewCart(view);

            startActivity(Intent(this@HomeActivity, MyAdsActivity::class.java))
        }


    }

    private fun checkAndDelete(key: String) {

        myAds!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.child(key).exists()) {
                    category!!.child(key).removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


//    private fun initSearchBar() {
//        searchBar.setHint("What do you need?")
//        searchBar.setTextHintColor(R.color.colorPrimaryDark)
//        searchBar.setTextColor(R.color.gen_black)
//        searchBar.setCardViewElevation(10)
//
//        searchBar.addTextChangeListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//            }
//
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//                //when user types a text we will change the suggest list
//                val suggest = ArrayList<String>()
//                for (search in suggestList) {
//                    if (search.toLowerCase().contains(searchBar.text.toLowerCase())) {
//                        suggest.add(search)
//
//                    }
//                    searchBar.lastSuggestions = suggest
//                }
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//
//            }
//        })
//
//        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
//            override fun onSearchStateChanged(enabled: Boolean) {
//
//                if (!enabled)
//                    recyclerView_menu!!.adapter = adapter
//            }
//
//            override fun onSearchConfirmed(text: CharSequence) {
//
//                startSearch(text)
//            }
//
//            override fun onButtonClicked(buttonCode: Int) {
//
//            }
//        })
//    }


    private fun initSwipeLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutHome)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light)
        swipeRefreshLayout.setOnRefreshListener {
            if (Common.isConnectedToInternet(baseContext)) {
                //                    loadData();
                loadList()
            } else {
                Toasty.error(baseContext, "please check your internet connection").show()
                //Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

        swipeRefreshLayout.post {
            if (Common.isConnectedToInternet(baseContext)) {
                //                    loadData();
                loadList()
            } else {
                Toasty.error(baseContext, "please check your internet connection").show()
                //Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }


//    private fun startSearch(text: CharSequence) {
//
//        val searchByTitle = category!!.orderByChild("title").equalTo(text.toString())
//
//        val options = FirebaseRecyclerOptions.Builder<Post>()
//                .setQuery(searchByTitle, Post::class.java)
//                .build()
//        searchAdapter = object : FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
//            override fun onBindViewHolder(holder: HomeViewHolder, position: Int, model: Post) {
//
//                holder.location.setText(model.location)
//                holder.price.setText(model.price)
//                holder.txtMenuName.setText(model.title)
//
//                Picasso.with(baseContext)
//                        .load(model.imageurl)
//                        .into(holder.imageView)
//
//                holder.setItemClickListener(object : ItemClickListener {
//                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
//                        val intent = Intent(this@HomeActivity, PostDetailActivity::class.java)
//                        intent.putExtra("postid", searchAdapter!!.getRef(position).key)
//                        intent.putExtra(Common.FROM_ACTIVITY, Common.HOME_ACTIVITY)
//                        startActivity(intent)
//                    }
//                })
//
//
//            }
//
//            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeViewHolder {
//                val itemView = LayoutInflater.from(viewGroup.context)
//                        .inflate(R.layout.menu_item, viewGroup, false)
//
//                return HomeViewHolder(itemView)
//            }
//        }
//
//        (searchAdapter as FirebaseRecyclerAdapter<Post, HomeViewHolder>).startListening()
//        recyclerView_menu!!.adapter = searchAdapter
//
//
//    }


    fun viewProfile(view: View) {
        startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
    }

//    fun viewCart(view: View) {
//        startActivity(Intent(this@HomeActivity, CartActivity::class.java))
//    }
//
//    fun Notifications(view: View) {
//        startActivity(Intent(this@HomeActivity, NotifcationActivity::class.java))
//    }


    private fun tapview() {

        TapTargetSequence(this)
                .targets(
                        //                        TapTarget.forView(findViewById(R.id.notifintro), "Notifications", "Latest offers will be available here !")
                        //                                .targetCircleColor(R.color.colorAccent)
                        //                                .titleTextColor(R.color.white)
                        //                                .titleTextSize(25)
                        //                                .descriptionTextSize(15)
                        //                                .descriptionTextColor(R.color.accent)
                        //                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                        //                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        //                                .tintTarget(true)
                        //                                .transparentTarget(true)
                        //                                .outerCircleColor(R.color.first),
                        TapTarget.forView(findViewById(R.id.view_profile), "Profile", "You can view and edit your profile here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third),
                        TapTarget.forView(findViewById(R.id.view_cart), "Your Uploads", "Here is Shortcut to your uploads!")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.second),
                        TapTarget.forView(findViewById(R.id.swipe_down), "Refresh", "Swipe down to refresh page!")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.green_light)
                )
                //                        TapTarget.forView(findViewById(R.id.visitingcards), "Categories", "Product Categories have been listed here !")
                //                                .targetCircleColor(R.color.colorAccent)
                //                                .titleTextColor(R.color.colorAccent)
                //                                .titleTextSize(25)
                //                                .descriptionTextSize(15)
                //                                .descriptionTextColor(R.color.accent)
                //                                .drawShadow(true)
                //                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                //                                .tintTarget(true)
                //                                .transparentTarget(true)
                //                                .outerCircleColor(R.color.fourth))
                .listener(object : TapTargetSequence.Listener {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    override fun onSequenceFinish() {
                        sesion!!.firstTime = (false)
                        Toasty.success(this@HomeActivity, " You are ready to go !", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {

                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // Boo
                    }
                }).start()

    }


    private fun loadUserDrawer() {
        userDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(auth!!.currentUser!!.uid)

        userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val user = dataSnapshot.getValue(User::class.java)
                    userDrawerName.text = user!!.name
                    userDrawerEmail.text = FirebaseAuth.getInstance().currentUser!!.email

                    val imageUri = Uri.parse(user.imageUrl)
                    Glide.with(baseContext)
                            .load(imageUri)
                            .into(userDrawerImage)
                } catch (e: Exception) {

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun loadList() {
        val searchByDate = category!!.orderByChild("postid").limitToLast(20)

        val options = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByDate, Post::class.java)
                .build()

        adapter = object : FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
            override fun onBindViewHolder(holder: HomeViewHolder, position: Int, model: Post) {


                //                checkAndDelete(adapter.getRef(position).getKey());
                //Uri imageUri = Uri.parse(model.getImageurl());

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

                        val intent = Intent(this@HomeActivity, PostDetailActivity::class.java)
                        intent.putExtra("postid", adapter!!.getRef(position).key)
                        intent.putExtra(Common.FROM_ACTIVITY, Common.HOME_ACTIVITY)
                        startActivity(intent)
                    }
                })
            }


            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeViewHolder {
                val itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.menu_item, viewGroup, false)

                return HomeViewHolder(itemView)
            }
        }

        (adapter as FirebaseRecyclerAdapter<Post, HomeViewHolder>).startListening()
        recyclerView_menu!!.adapter = adapter
        swipeRefreshLayout.isRefreshing = false

    }

    override fun onResume() {
        super.onResume()
        if (adapter != null)
            adapter!!.startListening()
        if (searchAdapter != null)
            searchAdapter!!.startListening()
        loadList()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null)
            adapter!!.stopListening()
        if (searchAdapter != null)
            searchAdapter!!.stopListening()
    }

    private fun loadSuggest() {
//        category!!.orderByChild("postid")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        for (postSnapshot in dataSnapshot.children) {
//                            val item = postSnapshot.getValue(Post::class.java)
//                            suggestList.add(item!!.title)
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//
//                    }
//                })
    }

    private fun verifyUserIsLoggedIn(): Boolean {
        var isLogged = false
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            isLogged = false
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return isLogged

        }else{
            isLogged = true

            return isLogged

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_category) {
            // Handle the camera action
            startActivity(Intent(this@HomeActivity, CategoriesActivity::class.java))
        } else if (id == R.id.nav_change_password) {
            startActivity(Intent(this@HomeActivity, ForgotPasswordActivity::class.java))

        } else if (id == R.id.nav_profile) {

            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))

        } else if (id == R.id.nav_posts) {
            startActivity(Intent(this@HomeActivity, PostsActivity::class.java))
        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@HomeActivity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
