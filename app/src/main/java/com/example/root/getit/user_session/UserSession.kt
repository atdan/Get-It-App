package com.example.root.getit.user_session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log


import com.example.root.getit.SignInActivity

import java.util.HashMap

class UserSession// Constructor
(// Context
        internal var context: Context) {
    // Shared Preferences
    internal var pref: SharedPreferences

    // Editor for Shared preferences
    internal var editor: SharedPreferences.Editor

    // Shared pref mode
    internal var PRIVATE_MODE = 0


    /**
     * Get stored session data
     */
    // user name
    // user email id
    // user phone number
    // user avatar
    // return user
    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user[KEY_NAME] = pref.getString(KEY_NAME, null)
            user[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)
            user[KEY_MOBiLE] = pref.getString(KEY_MOBiLE, null)
            user[KEY_PHOTO] = pref.getString(KEY_PHOTO, null)
            return user
        }

    /**
     * Quick check for login
     */
    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    var cartValue: Int
        get() = pref.getInt(KEY_CART, 0)
        set(count) {
            editor.putInt(KEY_CART, count)
            editor.commit()
        }

    var wishlistValue: Int
        get() = pref.getInt(KEY_WISHLIST, 0)
        set(count) {
            editor.putInt(KEY_WISHLIST, count)
            editor.commit()
        }

    var firstTime: Boolean?
        get() = pref.getBoolean(FIRST_TIME, true)
        set(n) {
            editor.putBoolean(FIRST_TIME, n!!)
            editor.commit()
        }

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    /**
     * Create login session
     */
    fun createLoginSession(name: String, email: String, mobile: String, photo: String) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true)

        // Storing name in pref
        editor.putString(KEY_NAME, name)

        // Storing email in pref
        editor.putString(KEY_EMAIL, email)

        // Storing phone number in pref
        editor.putString(KEY_MOBiLE, mobile)

        // Storing image url in pref
        editor.putString(KEY_PHOTO, photo)

        // commit changes
        editor.commit()
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    fun checkLogin() {
        // Check login status
        if (!this.isLoggedIn) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(context, SignInActivity::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Staring Login Activity
            context.startActivity(i)
        }

    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false)
        editor.commit()

        // After logout redirect user to Login Activity
        val i = Intent(context, SignInActivity::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        context.startActivity(i)
    }


    fun increaseCartValue() {
        val `val` = cartValue + 1
        editor.putInt(KEY_CART, `val`)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + cartValue + " ")
    }

    fun increaseWishlistValue() {
        val `val` = wishlistValue + 1
        editor.putInt(KEY_WISHLIST, `val`)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + cartValue + " ")
    }

    fun decreaseCartValue() {
        val `val` = cartValue - 1
        editor.putInt(KEY_CART, `val`)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + cartValue + " ")
    }

    fun decreaseWishlistValue() {
        val `val` = wishlistValue - 1
        editor.putInt(KEY_WISHLIST, `val`)
        editor.commit()
        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + cartValue + " ")
    }

    companion object {

        // Sharedpref file name
        private val PREF_NAME = "UserSessionPref"

        // First time logic Check
        val FIRST_TIME = "firsttime"

        // All Shared Preferences Keys
        private val IS_LOGIN = "IsLoggedIn"

        // User name (make variable public to access from outside)
        val KEY_NAME = "name"

        // Email address (make variable public to access from outside)
        val KEY_EMAIL = "email"

        // Mobile number (make variable public to access from outside)
        val KEY_MOBiLE = "mobile"

        // user avatar (make variable public to access from outside)
        val KEY_PHOTO = "photo"

        // number of items in our cart
        val KEY_CART = "cartvalue"

        // number of items in our wishlist
        val KEY_WISHLIST = "wishlistvalue"

        // check first time app launch
        val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }
}
