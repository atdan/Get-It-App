package com.example.root.getit.model

import com.google.firebase.database.Exclude
import java.util.*

class User {

    var name: String? = null
    var password: String? = null
    var email: String? = null
    var isStaff: String? = null
    var secureCode: String? = null
    var homeAddress: String? = null
    var phone: String? = null
    var imageUrl: String? = null
    var status: String? = null


    constructor() {

    }



    @Exclude
    fun toMap(): Map<String, String?> {
        val result = HashMap<String, String?>()
        result["password"] = password
        result["email"] = email
        result["isStaff"] = isStaff
        result["name"] = name
        result["phone"] = phone
        result["secureCode"] = secureCode
        result["homeAddress"] = homeAddress
        //result["userid"] = userid
        result["status"] = status
        result["imageUrl"] = imageUrl


        return result
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Post) return false
        val post = o as User?
        return name == post!!.name &&
                password == post.password &&
                email == post.email &&
                isStaff == post.isStaff &&
//                name == post.name &&
                phone == post.phone &&
                secureCode == post.secureCode &&
                homeAddress == post.homeAddress &&
                imageUrl == post.imageUrl &&
                status == post.status

    }

    override fun hashCode(): Int {

        return Objects.hash(name, password, email, isStaff, secureCode, homeAddress, phone, imageUrl, status)
    }



    override fun toString(): String {
        return super.toString()
    }

    constructor(name: String, password: String, email: String, isStaff: String, secureCode: String, homeAddress: String, phone: String, imageUrl: String, status: String) {
        this.name = name
        this.password = password
        this.email = email
        this.isStaff = isStaff
        this.secureCode = secureCode
        this.homeAddress = homeAddress
        this.phone = phone
        this.imageUrl = imageUrl
        this.status = status
    }


}
