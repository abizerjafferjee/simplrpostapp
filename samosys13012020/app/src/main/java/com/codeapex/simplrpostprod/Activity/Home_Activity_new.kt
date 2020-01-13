package com.codeapex.simplrpostprod.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import com.codeapex.simplrpostprod.Fragment.NotifiactionFragment
import com.codeapex.simplrpostprod.Fragment.ProfileFragment
import com.codeapex.simplrpostprod.Fragment.SettingsFragment
import com.codeapex.simplrpostprod.R
import com.codeapex.simplrpostprod.RetrofitApi.Api
import com.codeapex.simplrpostprod.RetrofitApi.Constants
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass
import com.google.gson.Gson
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.content_home__activity_new.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

class Home_Activity_new : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var profilefragment = ProfileFragment()
    private var notificationfragment = NotifiactionFragment()
    private var searchBusinessFragment = SearchBusinessFragment()
    private var settingsfragment = SettingsFragment()
    private var savedAddressListFragment = SavedAddressListFragment();
    private var sharedWithMeActivity = SharedWithMeActivity()
    private var fragmentManager = supportFragmentManager
    private var frgtActive: String = "profilefragment"
    private var intent_data: String? = null
    private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val REQUEST_CODE_ASK_PERMISSIONS = 123
    lateinit var btnFavorite: TextView
    lateinit var linearButtons: LinearLayout
    lateinit var linearHeader: LinearLayout
    lateinit var btn_editAddress: ImageButton
    lateinit var btnReceived: TextView
    lateinit var btnPersonal: TextView
    lateinit var progressBarImage: ProgressBar
    var sharedPreferences: SharedPreferences? = null
    var userImage: CircleImageView? = null
    var userId: String? = null
    var name: String? = null
    var emailId: String? = null
    var contactNumber: String? = null
    var dpimg: String? = null
    lateinit var fab: FloatingActionButton
    private var isEmailIdVerified: String? = null
    private var isContactNumberVerified: String? = null
    var question: String? = null
    var answer: String? = null
    var drawerLayout: DrawerLayout? = null
    var txtMsgProfilecomplete : TextView? = null

    companion object {
         var main_layout_home: CoordinatorLayout? = null
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home__new)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = this@Home_Activity_new!!.getSharedPreferences("Sesssion", MODE_PRIVATE)!!


        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (hasPermissions(*PERMISSIONS)) {
                startActivity(Intent(this@Home_Activity_new, AddLocation_Activity::class.java)
                        .putExtra("address_id", "")
                        .putExtra("user_id", "")
                        .putExtra("profile_img", "")
                        .putExtra("user_name", "")
                        .putExtra("plus_code", "")
                        .putExtra("public_private_tag", "")
                        .putExtra("qr_code_img", "")
                        .putExtra("street_img", "")
                        .putExtra("building_img", "")
                        .putExtra("entrance_img", "")
                        .putExtra("address_unique_link", "")
                        .putExtra("country", "")
                        .putExtra("city", "")
                        .putExtra("street", "")
                        .putExtra("building", "")
                        .putExtra("entrance", "")
                        .putExtra("latitude", "")
                        .putExtra("longitude", "")
                        .putExtra("direction_txt", "")
                        .putExtra("from", "add"))
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS)
                }
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        txtMsgProfilecomplete = findViewById(R.id.txt_msg_profileComplete)

        txt_msg_profileComplete.setOnClickListener {
            val intent = Intent(this@Home_Activity_new, EditProfileActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("userId", sharedPreferences!!.getString(Constants.userId,""))
            intent.putExtra("emailId", emailId)
            intent.putExtra("contactNumber", contactNumber)
            intent.putExtra("isEmailIdVerified", isEmailIdVerified)
            intent.putExtra("isContactNumberVerified", isContactNumberVerified)
            intent.putExtra("dpimg", "")
            intent.putExtra("question", question)
            intent.putExtra("answer", answer)
            startActivityForResult(intent, 1024)
        }


        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.frgtContainerr)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_setting, R.id.nav_about, R.id.nav_term,
                R.id.nav_policy, R.id.nav_faq, R.id.nav_feedback, R.id.nav_rateUs), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        //profile fragment
        fragmentManager.beginTransaction().add(R.id.frgtContainerr, profilefragment, "").commit()
        frgtActive = "profilefragment"


        linearButtons = findViewById(R.id.linearButtons)
        linearHeader = findViewById(R.id.linearHeader)
        progressBarImage = findViewById(R.id.progressBarImage)
        userImage = findViewById(R.id.userImage)
        btn_editAddress = findViewById(R.id.btn_editAddress)
        btnReceived = findViewById(R.id.btn_received)
        btnPersonal = findViewById(R.id.btn_personal)
        btnFavorite = findViewById(R.id.btn_favorite)
        main_layout_home = findViewById(R.id.main_layout_home)

        //get user profile data
        if(UtilityClass.isNetworkConnected(this@Home_Activity_new)) {
            getProfile()
        }else{
            progressBarImage.setVisibility(View.GONE);
        }

        btn_editAddress.setOnClickListener {
            val intent = Intent(this@Home_Activity_new, EditProfileActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("userId", sharedPreferences!!.getString(Constants.userId, ""))
            intent.putExtra("emailId", emailId)
            intent.putExtra("contactNumber", contactNumber)
            intent.putExtra("isEmailIdVerified", isEmailIdVerified)
            intent.putExtra("isContactNumberVerified", isContactNumberVerified)
            intent.putExtra("dpimg", "")
            intent.putExtra("question", question)
            intent.putExtra("answer", answer)
            startActivityForResult(intent, 1024)

        }

        btnPersonal.setOnClickListener {

            btn_favorite.isEnabled = false
            btn_received.isEnabled = false
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, profilefragment, "").commit()
            linearHeader.visibility = View.VISIBLE
            linearButtons.visibility = View.VISIBLE
            frgtActive = "profilefragment"
            fab.visibility = View.VISIBLE

            btnPersonal.setTextColor(resources.getColor(R.color.colorWhite))
            btnPersonal.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_left))
            setTextViewDrawableColorSelected(btnPersonal, "#FFFFFF")

            btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColor(btnFavorite, "#262626")

            btnReceived.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnReceived.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
            setTextViewDrawableColor(btnReceived, "#262626")

            btn_favorite.isEnabled = true
            btn_received.isEnabled = true
        }

        btnFavorite.setOnClickListener {
            btn_personal.isEnabled = false
            btn_received.isEnabled = false
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, savedAddressListFragment, "").commit()
            fab.visibility = View.GONE
            frgtActive = "savedAddressListFragment"
            btnFavorite.setTextColor(resources.getColor(R.color.colorWhite))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            setTextViewDrawableColorSelected(btnFavorite, "#FFFFFF")

            btnPersonal.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnPersonal.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_left))
            setTextViewDrawableColor(btnPersonal, "#262626")

            btnReceived.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnReceived.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
            setTextViewDrawableColor(btnReceived, "#262626")
            btn_personal.isEnabled = true
            btn_received.isEnabled = true
        }

        btnReceived.setOnClickListener {
            btn_personal.isEnabled = false
            btn_favorite.isEnabled = false
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, sharedWithMeActivity, "").commit()

            //linearHeader.visibility = View.GONE
            //linearButtons.visibility = View.GONE
            fab.visibility = View.GONE

            frgtActive = "receivedfragment"
            btnReceived.setTextColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColorSelected(btnReceived, "#FFFFFF")
            btnReceived.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_right))

            btnPersonal.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnPersonal.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_left))
            setTextViewDrawableColor(btnPersonal, "#262626")

            btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColor(btnFavorite, "#262626")
            btn_personal.isEnabled = true
            btn_favorite.isEnabled = true
        }

        val btnNotification: ImageButton = findViewById(R.id.btn_notification)
        btnNotification.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, notificationfragment, "").commit()
            frgtActive = "notificationfragment"
            linearHeader.visibility = View.GONE
            linearButtons.visibility = View.GONE
            fab.visibility = View.GONE

        }

        val btnSearch: ImageButton = findViewById(R.id.btn_search)
        btnSearch.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, searchBusinessFragment, "").commit()
            frgtActive = "searchBusinessFragment"
            linearHeader.visibility = View.GONE
            linearButtons.visibility = View.GONE
            fab.visibility = View.GONE
        }

        userImage!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@Home_Activity_new, EditProfileActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("userId", sharedPreferences!!.getString(Constants.userId, ""))
            intent.putExtra("emailId", emailId)
            intent.putExtra("contactNumber", contactNumber)
            intent.putExtra("isEmailIdVerified", isEmailIdVerified)
            intent.putExtra("isContactNumberVerified", isContactNumberVerified)
            intent.putExtra("dpimg", "")
            intent.putExtra("question", question)
            intent.putExtra("answer", answer)
            startActivityForResult(intent, 1024)
        })

    }

    @SuppressLint("RestrictedApi")
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.nav_home -> {
                fragmentManager.beginTransaction().replace(R.id.frgtContainerr, profilefragment, "").commit()
                frgtActive = "profilefragment"
                linearHeader.visibility = View.VISIBLE
                linearButtons.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE

                btnPersonal!!.setTextColor(resources.getColor(R.color.colorWhite))
                btnPersonal!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_left))
                setTextViewDrawableColorSelected(btnPersonal, "#FFFFFF")

                btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
                btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
                setTextViewDrawableColor(btnFavorite, "#262626")

                btnReceived!!.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
                btnReceived!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
                setTextViewDrawableColor(btnReceived!!, "#262626")
            }
            R.id.nav_setting -> {
                val intent = Intent(applicationContext, ProfileSettingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(intent)
            }
            R.id.nav_about -> {
                val intentt = Intent(applicationContext, AboutActivity::class.java)
                intentt.putExtra("toolbar_heading", "About Us")
                startActivity(intentt)
            }
            R.id.nav_term -> {
                val abourt = Intent(applicationContext, TermActivity::class.java)
                abourt.putExtra("toolbar_heading", "Privacy Policy")
                startActivity(abourt)
            }
            R.id.nav_policy -> {
                val privacy = Intent(applicationContext, PrivacyPolicyActivity::class.java)
                privacy.putExtra("toolbar_heading", "Terms & Conditions")
                startActivity(privacy)
            }
            R.id.nav_faq -> {
                val Faq = Intent(applicationContext, FAQActivity::class.java)
                Faq.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(Faq)
            }
            R.id.nav_feedback -> {
                val feedback = Intent(applicationContext, FeedbackActivity::class.java)
                feedback.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(feedback)
            }
            R.id.nav_rateUs -> {
                Log.e("packageName", applicationContext.packageName)
                val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
                val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(myAppLinkToMarket)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(applicationContext, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show()
                }

            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("ResourceType")
    private fun setTextViewDrawableColor(textView: TextView, color: String) {

        for (items: Drawable in textView.compoundDrawables) {
            Log.e("items", "items" + items)
            items?.setColorFilter(this@Home_Activity_new.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        }
    }

    @SuppressLint("ResourceType")
    private fun setTextViewDrawableColorSelected(textView: TextView, color: String) {

        for (items: Drawable in textView.compoundDrawables) {
            Log.e("items", "items" + items)
            items?.setColorFilter(this@Home_Activity_new.resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.frgtContainerr)
        drawerLayout!!.openDrawer(GravityCompat.START)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this@Home_Activity_new, "Some Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (this@Home_Activity_new!!.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {

        if (frgtActive != "profilefragment") {

            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, profilefragment, "").commit()
            frgtActive = "profilefragment"
            linearHeader.visibility = View.VISIBLE
            linearButtons.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE

            btnPersonal!!.setTextColor(resources.getColor(R.color.colorWhite))
            btnPersonal!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_left))
            setTextViewDrawableColorSelected(btnPersonal, "#FFFFFF")

            btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColor(btnFavorite, "#262626")

            btnReceived!!.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnReceived!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
            setTextViewDrawableColor(btnReceived!!, "#262626")

        } else {
            val alert = AlertDialog.Builder(this@Home_Activity_new)
            alert.setMessage("")
            alert.setCancelable(false)
            alert.setTitle("Are you sure you want to exit?")
            alert.setNegativeButton(
                    "No"
            ) { dialog, id -> dialog.cancel() }
            alert.setPositiveButton("Yes"
            ) { _, _ -> finish() }
            alert.show()
        }
    }

    private fun getProfile() = try {
        if (!UtilityClass.isNetworkConnected(this@Home_Activity_new!!)) {
        } else {
            var hashMap = HashMap<String, String>()
            hashMap[Constants.userId] = sharedPreferences!!.getString(Constants.userId, "")


            val call = RetrofitInstance.getdata().create(Api::class.java).getProfile(hashMap)
            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {

                    if (response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            Log.e("PROFILE DATA:", "" + jsonObject);
                            val result_code = jsonObject.getString(Constants.RESULT_CODE)

                            if (result_code == Constants.ZERO) {
                                //new Message().showSnack(root_lay, "No profile data available");

                            } else if (result_code == Constants.RESULT_CODE_MINUS_ONE) {
                                //Message().showSnack(root_lay, "This account has been deleted from this platform.")
                            } else if (result_code == Constants.RESULT_CODE_MINUS_TWO) {
                                // Message().showSnack(root_lay, "Something went wrong. Please try after some time.")

                            } else if (result_code == Constants.RESULT_CODE_MINUS_THREE) {
                                // Message().showSnack(root_lay, "No data found.")

                            } else if (result_code == Constants.RESULT_CODE_MINUS_FIVE) {
                                // Message().showSnack(root_lay, "This account has been blocked.")


                            } else if (result_code == Constants.RESULT_CODE_MINUS_SIX) {
                                //Message().showSnack(root_lay, "All fields not sent.")

                            } else if (result_code == Constants.RESULT_CODE_MINUS_SEVEN) {
                                //Message().showSnack(root_lay, " Please check the request method.")
                            } else if (result_code == "1.0" || result_code == "1") {
                                val result_data = jsonObject.getString(Constants.RESULT_DATA)
                                val jsonObject1 = JSONObject(result_data)
                                var userId = jsonObject1.getString("userId")

                                isEmailIdVerified = jsonObject1.getString("isEmailIdVerified")
                                isContactNumberVerified = jsonObject1.getString("isContactNumberVerified")
                                question = jsonObject1.getString("security_question")
                                answer = jsonObject1.getString("security_answer")

                                if (jsonObject1.has("name")) {
                                    name = jsonObject1.getString("name")
                                    var txtUserName: TextView = findViewById(R.id.txtUserName)
                                    txtUserName.text = name
                                }
                                if (jsonObject1.has("emailId")) {
                                    emailId = jsonObject1.getString("emailId")
                                }
                                contactNumber = jsonObject1.getString("contactNumber")
                                var txtMobileNumber: TextView = findViewById(R.id.txt_mobileNumber)
                                txtMobileNumber.text = contactNumber

                                var imgURL = "";
                                if (jsonObject1.has("profilePicURL")) {
                                    imgURL = jsonObject1.getString("profilePicURL")
                                } else {
                                    progressBarImage.visibility = View.GONE
                                }

                                if (name.equals("")) {
                                    txt_msg_profileComplete.visibility = View.VISIBLE
                                } else {
                                    txt_msg_profileComplete.visibility = View.GONE
                                }
                                if (jsonObject1.has("emailId")) {
                                    emailId = jsonObject1.getString("emailId")
                                    if (emailId != null || emailId!=("")) {
                                        txt_msg_profileComplete.visibility = View.GONE
                                    } else {
                                        txt_msg_profileComplete.visibility = View.VISIBLE
                                    }
                                }

                                if (question.equals("") || question.equals("null") || question==null) {
                                    txt_msg_profileComplete.visibility = View.VISIBLE
                                } else {
                                    txt_msg_profileComplete.visibility = View.GONE
                                }

                                val sharedPreferences = this@Home_Activity_new!!.getSharedPreferences("Sesssion", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString(Constants.userId, userId)
                                editor.putString(Constants.contactNumber, contactNumber)

                                if (jsonObject1.has("profilePicURL")) {
                                    editor.putString(Constants.profilePicURL, imgURL);
                                    if (imgURL != null && !imgURL.isEmpty()) {
                                        progressBarImage.visibility = View.VISIBLE;
                                        dpimg = Constants.IMG_URL + jsonObject1.getString("profilePicURL");

                                        Picasso.with(this@Home_Activity_new)
                                                .load(dpimg)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .placeholder(R.drawable.profileplchlder)
                                                .into(userImage, object : com.squareup.picasso.Callback {
                                                    override fun onSuccess() {
                                                        //set animations here
                                                        progressBarImage.setVisibility(View.GONE);
                                                        userImage!!.setOnClickListener {
                                                            var intent = Intent(this@Home_Activity_new, ImagePreviewActivity::class.java)
                                                            intent.putExtra("image", Constants.IMG_URL + imgURL);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    override fun onError() {
                                                        //do smth when there is picture loading error
                                                        progressBarImage.setVisibility(View.GONE)

                                                    }
                                                })

                                    } else {
                                        userImage!!.setImageResource(R.drawable.profileplchlder)

                                    }
                                } else {
                                    editor.putString(Constants.profilePicURL, "")
                                    progressBarImage.setVisibility(View.GONE)
                                    userImage!!.setImageResource(R.drawable.profileplchlder)
                                }

                                editor.commit()

                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {

                }
            })
        }
    } catch (e: Exception) {
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        hideKeyboard(this@Home_Activity_new)
        if (frgtActive==("savedAddressListFragment")){
            btn_personal.isEnabled = false
            btn_received.isEnabled = false
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, savedAddressListFragment, "").commit()
            fab.visibility = View.GONE
            frgtActive = "savedAddressListFragment"
            btnFavorite.setTextColor(resources.getColor(R.color.colorWhite))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            setTextViewDrawableColorSelected(btnFavorite, "#FFFFFF")

            btnPersonal.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnPersonal.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_left))
            setTextViewDrawableColor(btnPersonal, "#262626")

            btnReceived.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnReceived.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
            setTextViewDrawableColor(btnReceived, "#262626")
            btn_personal.isEnabled = true
            btn_received.isEnabled = true

        }
        else if (frgtActive==("receivedfragment")){
            btn_personal.isEnabled = false
            btn_favorite.isEnabled = false
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, sharedWithMeActivity, "").commit()

            fab.visibility = View.GONE

            frgtActive = "receivedfragment"
            btnReceived.setTextColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColorSelected(btnReceived, "#FFFFFF")
            btnReceived.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_right))

            btnPersonal.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnPersonal.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_left))
            setTextViewDrawableColor(btnPersonal, "#262626")

            btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColor(btnFavorite, "#262626")
            btn_personal.isEnabled = true
            btn_favorite.isEnabled = true

        }
        else if(frgtActive=="profilefragment"){
            if(UtilityClass.isNetworkConnected(this@Home_Activity_new)) {
                getProfile()
            }else{
                progressBarImage.setVisibility(View.GONE);
            }
            fragmentManager.beginTransaction().replace(R.id.frgtContainerr, profilefragment, "").commit()
            frgtActive = "profilefragment"
            linearHeader.visibility = View.VISIBLE
            linearButtons.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE

            btnPersonal!!.setTextColor(resources.getColor(R.color.colorWhite))
            btnPersonal!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_green_left))
            setTextViewDrawableColorSelected(btnPersonal, "#FFFFFF")

            btnFavorite.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnFavorite.setBackgroundColor(resources.getColor(R.color.colorWhite))
            setTextViewDrawableColor(btnFavorite, "#262626")

            btnReceived!!.setTextColor(resources.getColor(R.color.bottom_sheet_title_clr))
            btnReceived!!.setBackgroundDrawable(resources.getDrawable(R.drawable.tab_bg_white_right))
            setTextViewDrawableColor(btnReceived!!, "#262626")
        }
    }
}
