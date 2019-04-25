package com.example.root.getit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.root.getit.common.Common

class CategoriesActivity : AppCompatActivity() {

    internal lateinit var phonesAndAccessories: CardView
    internal lateinit var computersAndElectronics:CardView
    internal lateinit var furniture:CardView
    internal lateinit var services:CardView
    internal lateinit var automobile:CardView
    internal lateinit var fashion: CardView
    internal lateinit var children:CardView
    internal lateinit var footwear:CardView
    internal lateinit var musicalInstruments:CardView
    internal lateinit var others:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            val intent = Intent(this@CategoriesActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        })

        phonesAndAccessories = findViewById(R.id.phoneCategory)
        computersAndElectronics = findViewById(R.id.computer_category)
        furniture = findViewById(R.id.furniture_category)
        services = findViewById(R.id.service_category)
        automobile = findViewById(R.id.automobile_category)
        fashion = findViewById(R.id.fashion_category)
        children = findViewById(R.id.children_category)
        footwear = findViewById(R.id.shoe_category)
        musicalInstruments = findViewById(R.id.music_Category)
        others = findViewById(R.id.others_category)


        clickListeners()
    }

    private fun clickListeners() {
        phonesAndAccessories.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.MOBILE_CATEGORY)
            startActivity(phoneIntent)
        }

        computersAndElectronics.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.COMPUTER_CATEGORY)
            startActivity(phoneIntent)
        }

        furniture.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.FURNITURE_CATEGORY)
            startActivity(phoneIntent)
        }

        services.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.JOBS_CATEGORY)
            startActivity(phoneIntent)
        }

        automobile.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.VEHICLE_CATEGORY)
            startActivity(phoneIntent)
        }

        fashion.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.FASHION_CATEGORY)
            startActivity(phoneIntent)
        }

        children.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.CHILDREN_CATEGORY)
            startActivity(phoneIntent)
        }

        footwear.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.FOOTWEAR_CATEGORY)
            startActivity(phoneIntent)
        }

        musicalInstruments.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.MUSIC_CATEGORY)
            startActivity(phoneIntent)
        }
        others.setOnClickListener {
            val phoneIntent = Intent(this@CategoriesActivity, CategoryActivity::class.java)
            phoneIntent.putExtra("category", Common.OTHERS_CATEGORY)
            startActivity(phoneIntent)
        }
    }


    fun viewProfile(view: View) {
        startActivity(Intent(this@CategoriesActivity, ProfileActivity::class.java))
    }
}
