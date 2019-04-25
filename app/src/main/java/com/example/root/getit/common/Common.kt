package com.example.root.getit.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.example.root.getit.model.User


object Common {
    var current_user: User? = null

    var PHONE_TEXT = "userPhone"

    val INTENT_FOOD_ID = "FoodId"

    val BASE_URL = "https://fcm.googleapis.com/"

    val GOOGLE_API_URL = "https://maps.googleapis.com/"

    val MOBILE_CATEGORY = "Mobile Phones and Accessories"

    val FURNITURE_CATEGORY = "Furniture and Home Appliances"

    val COMPUTER_CATEGORY = "Computers and Accessories"

    val VEHICLE_CATEGORY = "Vehicles"

    val JOBS_CATEGORY = "Jobs and Services"

    val FASHION_CATEGORY = "Fashion and Beauty"

    val CHILDREN_CATEGORY = "Children and Toys"

    val FOOTWEAR_CATEGORY = "Footwear"

    val MUSIC_CATEGORY = "Musical Instruments"

    val OTHERS_CATEGORY = "Others"

    val FROM_ACTIVITY = "from_activity"

    val HOME_ACTIVITY = "home_activity"

    val MY_UPLOADS_ACTIVITY = "my_uploads_activity"

    val CATEGORIES_ACTIVITY = "category_activity"

    val DELETE = "Delete"

    val UPDATE = "Update"

    val NODE_POST_ITEM = "Post Item"

    val NODE_RAW_POST = "Raw Post"

    val SENDBIRD_APP_ID = "1C29715F-57D4-4E74-9782-68650D1213E4"


    val USER_KEY = "User"
    val PASSWORD_KEY = "Password"


    fun convertCodeToStatus(status: String): String {
        return if (status == "0") {
            "Order Placed"
        } else if (status == "1") {
            "On my way!"
        } else
            "shipped"
    }

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            val infos = connectivityManager.allNetworkInfo

            if (infos != null) {
                for (i in infos.indices) {
                    if (infos[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

}
