package com.example.root.getit

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.root.getit.common.Common
import com.example.root.getit.model.Post
import com.example.root.getit.utils.Utility
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.jaredrummler.materialspinner.MaterialSpinner
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.*

class PostsActivity : AppCompatActivity() {

    private val TAG = "PostsActivity"

    internal lateinit var title: TextInputEditText
    internal lateinit var price:TextInputEditText

    internal lateinit var name:TextInputEditText
    internal lateinit var phoneNumber:TextInputEditText
    internal lateinit var description:TextInputEditText
    internal lateinit var location:TextInputEditText
    internal lateinit var categorySpinner: MaterialSpinner


    internal lateinit var mSettings: SharedPreferences
    internal var user: FirebaseUser? = null

    internal lateinit var uid: String
    internal lateinit var submitButton: Button
    internal lateinit var addImageButton:Button

    internal lateinit var database: FirebaseDatabase
    internal lateinit var categories: DatabaseReference
    internal lateinit var postRef:DatabaseReference
    internal lateinit var rawPostRef:DatabaseReference
    internal lateinit var storage: FirebaseStorage
    internal lateinit var storageReference: StorageReference

    internal var saveUri: Uri? = null

    internal var mArrayUri = ArrayList<String>()

    internal var mobileN = ""
    internal var titleS = ""
    internal var descriptionS = ""
    internal var nameS = ""
    internal var priceS = ""
    internal var locationS = ""
    internal lateinit var categoryS:String

    val PICK_IMAGE_REQUEST = 71

    internal var downloadUri: Uri? = null
    internal lateinit var uploadTask: UploadTask

    internal var mDialog: ProgressDialog? = null

    internal var categoriesArray = arrayOf<String>(Common.MOBILE_CATEGORY, Common.FURNITURE_CATEGORY, Common.COMPUTER_CATEGORY, Common.VEHICLE_CATEGORY, Common.JOBS_CATEGORY, Common.FASHION_CATEGORY, Common.CHILDREN_CATEGORY, Common.FOOTWEAR_CATEGORY, Common.MUSIC_CATEGORY)

    internal var formattedDate = ""

    internal lateinit var userId: String

    internal lateinit var addImage: ImageView

    internal var filePath: Uri? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        try {
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            Objects.requireNonNull<ActionBar>(supportActionBar).setTitle("Upload Advert")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onCreate: " + e.message)
        }



//        mFirebaseDatabase = FirebaseDatabase.getInstance().reference
//        mFirebaseStorage = FirebaseStorage.getInstance().reference

        //init Firebase
        database = FirebaseDatabase.getInstance()
        categories = database.getReference("Category")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        postRef = database.getReference("Post Item")
        rawPostRef = database.getReference("Post Item")

        user = FirebaseAuth.getInstance().currentUser
//        uploadImage = findViewById(R.id.add_photo);


        initViews()

        init()
    }

    private fun initViews() {
        title = findViewById(R.id.title)
        price = findViewById(R.id.price)
        name = findViewById(R.id.name)
        description = findViewById(R.id.description)
        phoneNumber = findViewById(R.id.mobile_number)
        submitButton = findViewById(R.id.submit)
        location = findViewById(R.id.location)
        //numberOfImagesChosen = findViewById(R.id.txtNumberOfImagesChosen);

        database = FirebaseDatabase.getInstance()

        addImageButton = findViewById(R.id.add_image)

        mDialog = ProgressDialog(this)

        categorySpinner = findViewById(R.id.categorySpinner)

        categorySpinner.setItems(*categoriesArray)

        addImage = findViewById(R.id.add_image_img)

        //postSlider = findViewById(R.id.images_slider_post);
        //postSlider.setAdapter(new PostSliderAdapter());

        addImage.setOnClickListener { }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mSettings = getSharedPreferences(user!!.getUid(), Context.MODE_PRIVATE)
        val username = mSettings.getString("name", "")
        val mobileNumber = mSettings.getString("mobile", "")
        val userLocation = mSettings.getString("location", "")
        name.setText(username)
        phoneNumber.setText(mobileNumber)
        location.setText(userLocation)
        if (user != null) {
            val userEmail = user!!.getEmail()

            uid = user!!.getUid()


        }
        if (!Utility.isNetworkAvailable(this)) {

            Toast.makeText(this,
                    "Please check internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    fun validate(): Boolean {
        var valid = true

        mobileN = phoneNumber.text!!.toString()
        titleS = title.text!!.toString()
        descriptionS = description.text!!.toString()
        nameS = name.text!!.toString()
        priceS = price.text!!.toString()
        locationS = location.text!!.toString()


        if (mobileN.isEmpty() || mobileN.length != 11) {
            phoneNumber.error = "Please enter valid number"
            valid = false
        } else {
            phoneNumber.error = null
        }


        if (titleS.isEmpty() || titleS.length < 5) {

            title.error = "Please enter title not less than 5 letters"
            valid = false
        } else {
            title.error = null

        }

        if (descriptionS.isEmpty() || descriptionS.length < 10) {

            description.error = "Please enter description not less than 10 letters"
            valid = false
        } else {
            description.error = null

        }

        if (nameS.isEmpty()) {

            name.error = "Please enter your name"
            valid = false
        } else {
            name.error = null

        }

        if (priceS.isEmpty()) {

            price.error = "Please enter price"
            valid = false
        } else {
            price.error = null

        }
        if (locationS.isEmpty()) {
            location.error = "Please enter your location"
            valid = false
        } else {
            location.error = null
        }


        return valid
    }

    private fun init() {


        submitButton.setOnClickListener {
            if (validate()) {
                titleS = title.text!!.toString()
                descriptionS = description.text!!.toString()
                mobileN = phoneNumber.text!!.toString()
                categoryS = categoriesArray[categorySpinner.selectedIndex]
                priceS = price.text!!.toString()
                nameS = name.text!!.toString()

                Log.d(TAG, "onClick: submit button clicked")
                uploadImage()
                resetFields()
            }
        }


        addImageButton.setOnClickListener { chooseImage() }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun isEmpty(s: String): Boolean {
        return s == ""
    }


    private fun resetFields() {

        name.setText("")
        description.setText("")
        price.setText("")
        title.setText("")
        phoneNumber.setText("")
        location.setText("")
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }

    override fun onPause() {
        super.onPause()
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }

    private fun uploadImage() {
        if (filePath != null) {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("please wait")
            progressDialog.show()

            val imageName = UUID.randomUUID().toString()
            val postNumber = UUID.randomUUID().toString()
            val currentUser = Objects.requireNonNull<FirebaseUser>(FirebaseAuth.getInstance().currentUser).uid
            val ref = storageReference.child("images/" +
                    currentUser + "/user posts/" + imageName + postNumber)

            uploadTask = ref.putFile(filePath!!)


            val uriTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw Objects.requireNonNull<Exception>(task.exception)
                }
                ref.downloadUrl
            }
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadUri = task.result

                            // database.getReference(Common.NODE_POST_ITEM).child(user.getUid()).child("imageUrl")
                            //                                        .push().setValue(downloadUri);

                            val c = Calendar.getInstance().time


                            val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                            formattedDate = df.format(c)
                            categories = database.getReference(Common.NODE_POST_ITEM).child(categoriesArray[categorySpinner.selectedIndex])
                            postRef = database.getReference(Common.NODE_POST_ITEM).child(user!!.getUid())
                            rawPostRef = database.getReference(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST)
                            val key = categories.push().key
                            val keyP = postRef.push().key
                            val keyR = rawPostRef.push().key


                            userId = user!!.getUid()


                            val post = Post(titleS, priceS, descriptionS, categoryS, nameS, mobileN,
                                    userId, formattedDate, locationS, downloadUri.toString())

                            val postValues = post.toMap()
                            val postValues2 = post.toMap()
                            val postValues3 = post.toMap()


                            val childValues = HashMap<String, Any>()
                            val childValues2 = HashMap<String, Any>()
                            val childValues3 = HashMap<String, Any>()

                            assert(key != null)
                            childValues[key!!] = postValues
                            assert(keyP != null)
                            childValues2[keyP!!] = postValues2
                            assert(keyR != null)
                            childValues3[keyR!!] = postValues3

                            categories.updateChildren(childValues)
                            postRef.updateChildren(childValues2)
                            rawPostRef.updateChildren(childValues3)

                            startActivity(Intent(this@PostsActivity, HomeActivity::class.java))
                            finish()

                            progressDialog.dismiss()
                        }
                    }.addOnFailureListener { }

        }
    }

//    @RequiresApi(Build.VERSION_CODES.M)
    private fun chooseImage() {


            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                if (data.data != null) {

                    filePath = data.data

                    saveUri = filePath

                    CropImage.activity(filePath)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)


                } else {

                }
            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show()
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                assert(result != null)
                saveUri = result!!.uri
                addImage.setImageURI(saveUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert(result != null)
                Log.e(TAG, "onActivityResult: " + result!!.error)
            }
        }
    }
}
