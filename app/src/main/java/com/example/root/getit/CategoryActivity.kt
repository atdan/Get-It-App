package com.example.root.getit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.root.getit.common.Common
import com.example.root.getit.interfaces.ItemClickListener
import com.example.root.getit.model.Post
import com.example.root.getit.view_holder.HomeViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.picasso.Picasso
import java.util.ArrayList

class CategoryActivity : AppCompatActivity() {

    internal  var searchImage: ImageView? = null

    internal  var profileImage:ImageView? = null

    internal var searchbarB = false

    internal lateinit var categoryText: TextView

    internal var categoryString: String? = ""

    internal lateinit var searchBar: MaterialSearchBar

    internal lateinit var swipeRefreshLayout: SwipeRefreshLayout

    internal lateinit var recyclerView: RecyclerView

    internal lateinit var layoutManager: RecyclerView.LayoutManager

    internal lateinit var database: FirebaseDatabase
    internal lateinit var food_list: DatabaseReference
    internal lateinit var homeAds:DatabaseReference


    internal lateinit var adapter: FirebaseRecyclerAdapter<Post, HomeViewHolder>


    //Searchbar functionality
    internal lateinit var searchAdapter: FirebaseRecyclerAdapter<Post, HomeViewHolder>

    internal var suggestList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            val intent = Intent(this@CategoryActivity, CategoriesActivity::class.java)

            startActivity(intent)
        })

        init()

        if (intent != null) {
            categoryString = intent.getStringExtra("category")

        }
        if (!categoryString!!.isEmpty() && categoryString != null) {
            categoryText.text = categoryString

            if (Common.isConnectedToInternet(baseContext)) {
                loadCategoryList(categoryString!!)
            } else
                Toast.makeText(baseContext, "Please check your internet connection", Toast.LENGTH_SHORT).show()

        }

        searchImage!!.setOnClickListener {
            if (!searchbarB) {
                searchBar.visibility = View.GONE
                searchbarB = true
            } else if (searchbarB) {
                searchBar.visibility = View.VISIBLE
                searchbarB = false
            }
        }


        setUpSwipeRefreshLayout()

        setUpSearchBar()

        loadSuggest()

        loadCategoryList(this!!.categoryString!!)
    }

    private fun setUpSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            //get posn
            if (intent != null) {
                categoryString = intent.getStringExtra("category")

            }
            if (!categoryString!!.isEmpty() && categoryString != null) {

                if (Common.isConnectedToInternet(baseContext)) {
                    loadCategoryList(categoryString!!)
                } else
                    Toast.makeText(baseContext, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                return@OnRefreshListener

            }
        })

        swipeRefreshLayout.post(Runnable {
            //get posn
            if (intent != null) {
                categoryString = intent.getStringExtra("category")

            }
            if (!categoryString!!.isEmpty() && categoryString != null) {

                if (Common.isConnectedToInternet(baseContext)) {
                    loadCategoryList(categoryString!!)
                } else
                    Toast.makeText(baseContext, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                return@Runnable

            }
        })

    }

    private fun init() {

        //init firebase
        database = FirebaseDatabase.getInstance()

        food_list = database.getReference("Post Item")

        homeAds = database.getReference("Post Item").child(Common.NODE_RAW_POST)

        searchImage = findViewById(R.id.search)

        profileImage = findViewById(R.id.profileImage)

        searchBar = findViewById(R.id.searchBar)

        categoryText = findViewById(R.id.category_text)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        recyclerView = findViewById(R.id.recycler_category_list)

        recyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager


    }

    private fun startSearch(text: CharSequence) {
        val searchByTitle = food_list.child(this!!.categoryString!!).orderByChild("Title").equalTo(text.toString())

        val options = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByTitle, Post::class.java)
                .build()

        searchAdapter = object : FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
            override fun onBindViewHolder(viewHolder: HomeViewHolder, position: Int, model: Post) {
                viewHolder.txtMenuName.setText(model.title)
                viewHolder.location.setText(model.location)
                viewHolder.price.setText(model.price)

                Picasso.with(baseContext).load(model.imageurl)
                        .into(viewHolder.imageView)

                viewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                        val intent = Intent(this@CategoryActivity, PostDetailActivity::class.java)
                        intent.putExtra("postid", searchAdapter.getRef(position).key)
                        intent.putExtra(Common.FROM_ACTIVITY, Common.CATEGORIES_ACTIVITY)
                        intent.putExtra("categoryString", categoryString)
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

        searchAdapter.startListening()
        recyclerView.adapter = searchAdapter
    }

    private fun setUpSearchBar() {
        searchBar.setHint("Enter what you are searching for")
        searchBar.lastSuggestions = suggestList
        searchBar.setCardViewElevation(10)

        loadCategoryList(this!!.categoryString!!)

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                //when user types a text we will change the suggest list
                val suggest = ArrayList<String>()
                for (search in suggestList) {
                    if (search.toLowerCase().contains(searchBar.text.toLowerCase())) {
                        suggest.add(search)

                    }
                    searchBar.lastSuggestions = suggest
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                // when searchbar is closed
                //restore original adapter

                if (!enabled)
                    recyclerView.adapter = adapter
            }

            override fun onSearchConfirmed(text: CharSequence) {

                //when search is finished
                //show result of search adapter
                startSearch(text)
            }

            override fun onButtonClicked(buttonCode: Int) {

            }
        })
    }

    private fun loadSuggest() {

        food_list.child(this!!.categoryString!!).orderByChild("postid")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {

                            val item = postSnapshot.getValue(Post::class.java)
                            if (item != null) {
                                suggestList.add(item.title)
                            }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
    }

    private fun loadCategoryList(categoryString: String) {

        val searchByTitle = homeAds.orderByChild("category").equalTo(categoryString)

        val foodOptions = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByTitle, Post::class.java)
                .build()

        adapter = object : FirebaseRecyclerAdapter<Post, HomeViewHolder>(foodOptions) {
            override fun onBindViewHolder(viewHolder: HomeViewHolder, position: Int, model: Post) {


                //                checkAndDelete(adapter.getRef(position).getKey());


                viewHolder.price.setText(model.price)
                viewHolder.location.setText(model.location)
                viewHolder.txtMenuName.setText(model.title)

                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.image_processing)
                requestOptions.error(R.drawable.no_image)


                Glide.with(baseContext)
                        .applyDefaultRequestOptions(requestOptions)
                        .setDefaultRequestOptions(requestOptions)
                        .load(model.imageurl)
                        .into(viewHolder.imageView)


                viewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                        val intent = Intent(this@CategoryActivity, PostDetailActivity::class.java)
                        intent.putExtra("postid", adapter.getRef(position).key)
                        intent.putExtra(Common.FROM_ACTIVITY, Common.CATEGORIES_ACTIVITY)
                        intent.putExtra("categoryString", categoryString)
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
        adapter.startListening()
        recyclerView.adapter = adapter
    }

    fun viewProfile(view: View) {
        startActivity(Intent(this@CategoryActivity, ProfileActivity::class.java))
    }
}
