package com.example.root.getit

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.root.getit.model.User
import com.example.root.getit.utils.PhotoFullPopupWindow
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import java.util.*

class ProfileActivity : AppCompatActivity() {

    internal lateinit var profile_image: CircleImageView

    internal lateinit var name: TextView
    internal lateinit var phoneNumber:TextView
    internal lateinit var email:TextView
    internal lateinit var address:TextView

    private val REQUEST_CAMERA = 123
    private val PICKFILE_REQUEST_CODE = 12
    private val TAG = "EditProfileActivty"
    internal lateinit var saveProfileButton: Button

    internal lateinit var changeProfileImage: com.google.android.material.floatingactionbutton.FloatingActionButton
    internal lateinit var editName: EditText
    internal lateinit var editMobile:EditText
    internal lateinit var editAddress:EditText
    internal lateinit var auth: FirebaseAuth

    internal var firebaseUser: FirebaseUser? = null

    internal lateinit var authStateListener: FirebaseAuth.AuthStateListener
    internal lateinit var userDatabase: DatabaseReference


    internal lateinit var storage: FirebaseStorage
    internal lateinit var storageReference: StorageReference

    internal var user: User? = null

    internal lateinit var uploadTask: UploadTask
    internal var imageHoldUri: Uri? = null

    internal lateinit var progressDialog: ProgressDialog
    internal lateinit var nameS: String
    internal var mobileS:String? = null
    internal var photoUrlS:String? = null
    internal lateinit var addressS:String

    private val IMAGE_REQUEST = 1
    private var saveUri: Uri? = null
    //private StorageTask uploadTask;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        initFirebase()
        loadProfile()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser
        authStateListener = FirebaseAuth.AuthStateListener {
            //check user presence
            val user = firebaseUser

            if (user != null) {
                finish()

                val moveToHome = Intent(this@ProfileActivity, HomeActivity::class.java)
                moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(moveToHome)
            }
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        userDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(auth!!.currentUser!!.uid)
        Log.d("User Id" , ": "+ auth.currentUser!!.uid)
        userDatabase.keepSynced(true)


        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)
                name.text = user!!.name
                //name.text = FirebaseAuth.getInstance().currentUser!!.displayName
                email.text = FirebaseAuth.getInstance().currentUser!!.email
                phoneNumber.text = user!!.phone
                //phoneNumber.text = FirebaseAuth.getInstance().currentUser!!.phoneNumber
                address.text = user!!.homeAddress


                nameS = name.text.toString()

                mobileS = phoneNumber.text.toString()

                addressS = address.text.toString()

                changeProfileImage.setOnClickListener { profilePictureSelection() }


                //Uri imageUri = Uri.parse(user != null ? user.getImageurl() : null);
                if (user != null) {
                    val imageUri = user.imageUrl
                    if (imageUri == null || imageUri == "default") {
                        Glide.with(baseContext)
                                .load(R.drawable.user_profile_pic)
                                .into(profile_image)

                        photoUrlS = null
                    } else {
                        Glide.with(baseContext)
                                .load(Uri.parse(imageUri))
                                .into(profile_image)

                        photoUrlS = imageUri
                    }


                } else {
                    //Toasty.info(this@ProfileActivity, "No Profile Picture", Toast.LENGTH_SHORT).show()

                    Glide.with(baseContext)
                            .load(R.drawable.user_profile_pic)
                            .into(profile_image)

                    photoUrlS = null

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }

    private fun initViews() {
        profile_image = findViewById(R.id.profileImage)

        name = findViewById(R.id.profileName)

        phoneNumber = findViewById(R.id.phone_profile)

        email = findViewById(R.id.emailProfile)

        address = findViewById(R.id.address_profile)

        saveProfileButton = findViewById(R.id.editProfileButton)

        changeProfileImage = findViewById(R.id.change_profile_pic)


        saveProfileButton.setOnClickListener {
            uploadProfilePicToFirebaseStorage()
        }

        name.setOnClickListener { rename() }

        phoneNumber.setOnClickListener {
            Log.e("Profile Activity", "onClick: Phone Clicked")
            changeMobile()
        }

        address.setOnClickListener { changeAddress() }

        profile_image.setOnClickListener { view -> showFullImage(view) }

    }

    private fun loadProfile() {

    }

    private fun profilePictureSelection() {

        val items = arrayOf<CharSequence>("Take Photos", "Choose from gallery", "Cancel")


        val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setTitle("Add photos")

        //set items and listeners
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == "Take Photos") {
                cameraIntent()
            } else if (items[i] == "Choose from gallery") {
                galleryIntent()
            } else {
                dialogInterface.dismiss()
            }
        }
        builder.show()

    }

    private fun galleryIntent() {
        Log.d(TAG, "onClick: accessing phone memory")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)


    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun uploadProfilePicToFirebaseStorage() {
        if (saveUri != null) {
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Saving profile")
            progressDialog.setMessage("please wait...")
            progressDialog.show()

            val childRef = storageReference.child("Users")
                    .child(firebaseUser!!.uid)
                    .child("profile picture")
                    .child(Objects.requireNonNull(imageHoldUri!!.lastPathSegment))

            uploadTask = childRef.putFile(saveUri!!)

            val uriTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw Objects.requireNonNull<Exception>(task.exception)
                } else {
                    childRef.downloadUrl
                }
            }.addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    Toasty.success(baseContext, "Upload Successful").show()
                    val saveUriLoc = task.result

                    userDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)

                            user!!.imageUrl = (saveUriLoc!!.toString())

                            userDatabase.setValue(user)
                            startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))

//                            if (user != null) {
//
//
//                            } else
//                                Toasty.error(baseContext, "No user").show()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                } else {

                    Toasty.error(baseContext, "Image upload failed").show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val imageUri = data!!.data
            saveUri = imageUri

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)

            if (imageHoldUri != null) {
                progressDialog.setTitle("Saving profile")
                progressDialog.setMessage("please wait...")
                progressDialog.show()
                val childRef = storageReference.child("Users").child((imageHoldUri!!.lastPathSegment))

                val profilePicUrl = imageHoldUri!!.lastPathSegment

                childRef.putFile(imageHoldUri!!).addOnSuccessListener { taskSnapshot ->
                    val urlTask = taskSnapshot.storage.downloadUrl
                    while (!urlTask.isSuccessful);
                    //Uri downloadUrl = urlTask.getResult();
                    val imageUrl = urlTask.result
                    saveUri = imageUrl

                    userDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)


                            if (user != null) {
                                assert(imageUrl != null)
                                user.imageUrl = (imageUrl!!.toString())
                            }

                            userDatabase.setValue(user)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })






                    userDatabase.child("userId").setValue(auth.currentUser!!.uid)

                    //userDatabase.child("imageUrl").setValue(imageUrl.toString());


                    progressDialog.dismiss()
                }
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

            assert(data != null)
            val imageUri = data!!.data

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)

            if (imageHoldUri != null) {
                progressDialog.setTitle("Saving profile")
                progressDialog.setMessage("please wait...")
                progressDialog.show()
                val childRef = storageReference.child("User Profile").child(Objects.requireNonNull(imageHoldUri!!.lastPathSegment))

                val profilePicUrl = imageHoldUri!!.lastPathSegment

                childRef.putFile(imageHoldUri!!).addOnSuccessListener { taskSnapshot ->
                    val urlTask = taskSnapshot.storage.downloadUrl
                    while (!urlTask.isSuccessful);
                    //Uri downloadUrl = urlTask.getResult();
                    val imageUrl = urlTask.result

                    userDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)


                                assert(imageUrl != null)
                                user!!.imageUrl = (imageUrl!!.toString())

                            userDatabase.setValue(user)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })






                    userDatabase.child("userId").setValue(auth.currentUser!!.uid)

                    //userDatabase.child("imageUrl").setValue(imageUrl.toString());


                    progressDialog.dismiss()
                }
            }

        }

        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                assert(result != null)
                imageHoldUri = result!!.uri

                profile_image.setImageURI(imageHoldUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = if (result != null) result.error else null
            }
        }
    }


    private fun changeAddress() {
        val alertDialog = AlertDialog.Builder(this@ProfileActivity)
        alertDialog.setTitle("Set Address")
        alertDialog.setMessage("Enter your Address")

        val inflater = this@ProfileActivity.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.change_address, null)

        editAddress = add_menu_layout.findViewById(R.id.change_address)

        alertDialog.setView(add_menu_layout)
        alertDialog.setIcon(R.drawable.ic_home_black_48dp)

        editAddress.setText(addressS)

        alertDialog.setPositiveButton("Save") { dialogInterface, i ->
            userDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    var user: User? = User()
                    user = dataSnapshot.getValue(User::class.java)

                    user?.homeAddress = (editAddress.text.toString())

                    userDatabase.setValue(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

        alertDialog.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }

        alertDialog.show()
    }

    fun showFullImage(view: View) {

        if (photoUrlS != null) {
            PhotoFullPopupWindow(this, R.layout.popup_photo_full, view, photoUrlS, null)
        } else {
            PhotoFullPopupWindow(this, R.layout.popup_photo_full, view, photoUrlS, null)
        }


    }


    fun rename() {

        val alertDialog = AlertDialog.Builder(this@ProfileActivity)
        alertDialog.setTitle("Set Name")
        alertDialog.setMessage("Enter your Name")

        val inflater = this@ProfileActivity.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.change_name, null)

        editName = add_menu_layout.findViewById(R.id.change_name)

        alertDialog.setView(add_menu_layout)
        alertDialog.setIcon(R.drawable.ic_perm_identity_black_48dp)

        editName.setText(nameS)

        alertDialog.setPositiveButton("Save") { dialogInterface, i ->
            userDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)

                    user?.name = editName.text.toString()

                    userDatabase.setValue(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

        alertDialog.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }

        alertDialog.show()
    }

    fun changeMobile() {
        val alertDialog = AlertDialog.Builder(this@ProfileActivity)
        alertDialog.setTitle("Set Mobile Number")
        alertDialog.setMessage("Enter your Phone Number")

        val inflater = this@ProfileActivity.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.change_mobile, null)

        editMobile = add_menu_layout.findViewById(R.id.change_mobile)

        alertDialog.setView(add_menu_layout)
        alertDialog.setIcon(R.drawable.ic_phone_android_black_24dp)

        if (mobileS != null) {
            editMobile.setText(mobileS)
        }


        alertDialog.setPositiveButton("Save") { dialogInterface, i ->
            userDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)

                    try {
//                        user?.phone(editMobile.text.toString())
                        user?.phone = (editMobile.text.toString())
                    } catch (e: Exception) {

                    }


                    userDatabase.setValue(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    dialogInterface.dismiss()
                }
            })
        }

        alertDialog.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        alertDialog.show()
    }
}
