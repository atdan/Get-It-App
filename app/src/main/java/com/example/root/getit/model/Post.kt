package com.example.root.getit.model

import com.google.firebase.database.Exclude

import java.util.HashMap
import java.util.Objects

class Post {
    lateinit var title: String
    lateinit var price: String
    lateinit var description: String
    lateinit var category: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var userid: String
    lateinit var postid: String
    lateinit var imageurl: String
    lateinit var location: String

    constructor(title: String, price: String, description: String, category: String,
                name: String, phone: String, userid: String, postid: String, location: String,
                imageurl: String) {
        this.title = title
        this.price = price
        this.description = description
        this.category = category
        this.name = name
        this.phone = phone
        this.userid = userid
        this.postid = postid
        this.location = location
        this.imageurl = imageurl

    }

    constructor() {}

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["title"] = title
        result["price"] = price
        result["description"] = description
        result["name"] = name
        result["phone"] = phone
        result["postid"] = postid
        result["category"] = category
        result["userid"] = userid
        result["location"] = location
        result["imageurl"] = imageurl


        return result
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Post) return false
        val post = o as Post?
        return title == post!!.title &&
                price == post.price &&
                description == post.description &&
                category == post.category &&
                name == post.name &&
                phone == post.phone &&
                userid == post.userid &&
                postid == post.postid &&
                location == post.location &&
                imageurl == post.imageurl

    }

    override fun hashCode(): Int {

        return Objects.hash(title, price, description, category, name, phone, userid, postid, location, imageurl)
    }
}
