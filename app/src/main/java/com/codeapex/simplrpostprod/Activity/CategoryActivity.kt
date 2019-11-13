package com.codeapex.simplrpostprod.Activity

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeapex.simplrpostprod.Adapter.AdaterCategory
import com.codeapex.simplrpostprod.ModelClass.Category
import com.codeapex.simplrpostprod.R
import com.codeapex.simplrpostprod.RetrofitApi.Api
import com.codeapex.simplrpostprod.RetrofitApi.Constants
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.lyt_loading.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CategoryActivity : AppCompatActivity()  {


    var list = ArrayList<Category>()
    var duplicadateData = ArrayList<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        getCategories()


        back_press.setOnClickListener { finish() }
        txt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createNewList(s.toString())
            }
        })
    }

    fun createNewList(query: String) {
        duplicadateData.clear()
        recyclervieww.adapter?.notifyDataSetChanged()
        for (category in list) {
            if (category.categoryName.contains(query, true))
                duplicadateData.add(category)
        }
        recyclervieww.adapter?.notifyDataSetChanged()
    }

    fun getCategories() {
        val hashMap = HashMap<String, String>()
        lyt_loading.visibility = View.VISIBLE

        val call = RetrofitInstance.getdata().create(Api::class.java).getCategories(hashMap)
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.d(TAG, "nbewwww: $response")
                lyt_loading.visibility = View.GONE


                try {

                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val result_code = jsonObject.getString(Constants.RESULT_CODE)
                    if (result_code == Constants.RESULT_CODE_ONE) {

                        val result_data = jsonObject.getString(Constants.RESULT_DATA)

                        val jsonArray = JSONArray(result_data)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)

                            val categoryId = jsonObject1.getString("categoryId")

                            val categoryName = jsonObject1.getString("categoryName")


                            val category = Category(categoryId, categoryName)
                            list.add(category)



                        }
                        duplicadateData.addAll(list)
                        val linearLayoutManager = LinearLayoutManager(applicationContext)
                        recyclervieww.layoutManager = linearLayoutManager

                        val custom_adaterFriends = AdaterCategory(applicationContext, duplicadateData, object : AdaterCategory.CategorySelected {
                            override fun onCategorySelection(category: Category?) {
                                if (intent.extras != null) {
                                    if (intent.getBooleanExtra("isFromExplore",false) == true) {
                                        val intent = Intent()
                                        val bundle = Bundle()
                                        intent.putExtra("categoryId", category?.categoryId)
                                        intent.putExtra("categoryName", category?.categoryName)
                                        intent.putExtras(bundle)
                                        setResult(2,intent)
                                        finish()
                                    }
                                }
                                else {
                                    val intent = Intent()
                                    val bundle = Bundle()
                                    bundle.putSerializable("category", category)
                                    intent.putExtras(bundle)
                                    hideKeyboard(this@CategoryActivity)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }

                            }
                        })
                        recyclervieww.adapter = custom_adaterFriends

                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
                lyt_loading.visibility = View.GONE


            }
        })


        //    }
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

}
