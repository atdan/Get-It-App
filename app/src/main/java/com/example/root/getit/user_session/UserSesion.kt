//package com.example.root.getit.user_session
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.util.Log
//import com.example.root.getit.SignInActivity
//import java.util.HashMap
//
////class UserSesion(context: Context) {
//
//class UserSesion(context: Context) {
//
//
//        internal lateinit var pref: SharedPreferences
//        internal lateinit var editor: SharedPreferences.Editor
//        internal lateinit var context: Context
//
//    // Shared pref mode
//    internal var PRIVATE_MODE = 0
//
//    // Sharedpref file name
//    private val PREF_NAME = "UserSessionPref"
//
//    // First time logic Check
//    val FIRST_TIME = "firsttime"
//
//    // All Shared Preferences Keys
//    private val IS_LOGIN = "IsLoggedIn"
//
//    // User name (make variable public to access from outside)
//    val KEY_NAME = "name"
//
//    // Email address (make variable public to access from outside)
//    val KEY_EMAIL = "email"
//
//    // Mobile number (make variable public to access from outside)
//    val KEY_MOBiLE = "mobile"
//
//    // user avatar (make variable public to access from outside)
//    val KEY_PHOTO = "photo"
//
//    // number of items in our cart
//    val KEY_CART = "cartvalue"
//
//    // number of items in our wishlist
//    val KEY_WISHLIST = "wishlistvalue"
//
//    // check first time app launch
//    val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
//
//    // Constructor
//    fun UserSession(context: Context) {
//        this.context = context
//        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
//        editor = pref.edit()
//    }
//
//    /**
//     * Create login session
//     */
//    fun createLoginSession(name: String, email: String, mobile: String, photo: String) {
//        // Storing login value as TRUE
//        editor.putBoolean(IS_LOGIN, true)
//
//        // Storing name in pref
//        editor.putString(KEY_NAME, name)
//
//        // Storing email in pref
//        editor.putString(KEY_EMAIL, email)
//
//        // Storing phone number in pref
//        editor.putString(KEY_MOBiLE, mobile)
//
//        // Storing image url in pref
//        editor.putString(KEY_PHOTO, photo)
//
//        // commit changes
//        editor.commit()
//    }
//
//    /**
//     * Check login method wil check user login status
//     * If false it will redirect user to login page
//     * Else won't do anything
//     */
//    fun checkLogin() {
//        // Check login status
//        if (!this.isLoggedIn()) {
//            // user is not logged in redirect him to Login Activity
//            val i = Intent(context, SignInActivity::class.java)
//            // Closing all the Activities
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//            // Add new Flag to start new Activity
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//            // Staring Login Activity
//            context.startActivity(i)
//        }
//
//    }
//
//
//    /**
//     * Get stored session data
//     */
//    fun getUserDetails(): HashMap<String, String> {
//        val user = HashMap<String, String>()
//        // user name
//        user[KEY_NAME] = pref.getString(KEY_NAME, null)
//
//        // user email id
//        user[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)
//
//        // user phone number
//        user[KEY_MOBiLE] = pref.getString(KEY_MOBiLE, null)
//
//        // user avatar
//        user[KEY_PHOTO] = pref.getString(KEY_PHOTO, null)
//
//        // return user
//        return user
//    }
//
//    /**
//     * Clear session details
//     */
//    fun logoutUser() {
//        // Clearing all data from Shared Preferences
//        editor.putBoolean(IS_LOGIN, false)
//        editor.commit()
//
//        // After logout redirect user to Login Activity
//        val i = Intent(context, SignInActivity::class.java)
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        // Add new Flag to start new Activity
//        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        // Staring Login Activity
//        context.startActivity(i)
//    }
//
//    /**
//     * Quick check for login
//     */
//    // Get Login State
//    fun isLoggedIn(): Boolean {
//        return pref.getBoolean(IS_LOGIN, false)
//    }
//
//    fun getCartValue(): Int {
//        return pref.getInt(KEY_CART, 0)
//    }
//
//    fun getWishlistValue(): Int {
//        return pref.getInt(KEY_WISHLIST, 0)
//    }
//
//    fun getFirstTime(): Boolean? {
//        return pref.getBoolean(FIRST_TIME, true)
//    }
//
//    fun setFirstTime(n: Boolean?) {
//        editor.putBoolean(FIRST_TIME, n!!)
//        editor.commit()
//    }
//
//
//    fun increaseCartValue() {
//        val `val` = getCartValue() + 1
//        editor.putInt(KEY_CART, `val`)
//        editor.commit()
//        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + getCartValue() + " ")
//    }
//
//    fun increaseWishlistValue() {
//        val `val` = getWishlistValue() + 1
//        editor.putInt(KEY_WISHLIST, `val`)
//        editor.commit()
//        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + getCartValue() + " ")
//    }
//
//    fun decreaseCartValue() {
//        val `val` = getCartValue() - 1
//        editor.putInt(KEY_CART, `val`)
//        editor.commit()
//        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + getCartValue() + " ")
//    }
//
//    fun decreaseWishlistValue() {
//        val `val` = getWishlistValue() - 1
//        editor.putInt(KEY_WISHLIST, `val`)
//        editor.commit()
//        Log.e("Cart Value PE", "Var value : " + `val` + "Cart Value :" + getCartValue() + " ")
//    }
//
//    fun setCartValue(count: Int) {
//        editor.putInt(KEY_CART, count)
//        editor.commit()
//    }
//
//    fun setWishlistValue(count: Int) {
//        editor.putInt(KEY_WISHLIST, count)
//        editor.commit()
//    }
//
//    fun setFirstTimeLaunch(isFirstTime: Boolean) {
//        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
//        editor.commit()
//    }
//
//    fun isFirstTimeLaunch(): Boolean {
//
//        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
//    }
//
//
//
//}