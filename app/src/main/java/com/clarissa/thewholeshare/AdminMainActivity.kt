package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.fragments.*
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.Gson



class AdminMainActivity : AppCompatActivity() {

    lateinit var navbar_admin : BottomNavigationView
    //untuk user yang sedang login
    lateinit var userActive : User
    var unameActive = ""

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        navbar_admin = findViewById(R.id.navbar_admin)

        userActive = User(-1,"","","","","","",-1,"")

        //ambil data dari intent
        val usernameActive = intent.getStringExtra("active_username")
        if(usernameActive!=null){
            unameActive = usernameActive
            //getUserLoggedIn(usernameActive)
        }

        switchFragment(0)

        navbar_admin.setOnItemSelectedListener {

            if(it.itemId == R.id.item_home_admin){
                switchFragment(0)
            }else if(it.itemId == R.id.item_master_admin) {
                switchFragment(1)
            }else if(it.itemId == R.id.item_profile_admin){
                switchFragment(2)
            }

            return@setOnItemSelectedListener true
        }

    }

    //to get user who logged in
//    fun getUserLoggedIn(uname:String){
//        val strReq = object: StringRequest(
//            Method.GET,
//            "$WS_HOST/listUsers",
//            Response.Listener {
//                val obj: JSONArray = JSONArray(it)
//                println(obj.length())
//                for (i in 0 until obj.length()){
//                    val o = obj.getJSONObject(i)
//                    println(o)
//                    val id = o.getInt("id")
//                    val username = o.getString("username")
//                    val password = o.getString("password")
//                    val full_name = o.getString("full_name")
//                    val phone = o.getString("phone")
//                    val address = o.getString("address")
//                    val email = o.getString("email")
//                    val role = o.getInt("role")
//                    val deleted_at = o.get("deleted_at").toString()
//                    val u = User(
//                        id,username,password,full_name,phone,address,email,role,deleted_at
//                    )
//                    if(username==uname) {
//                        userActive = u
//                        break
//                    }
//                }
//                println(userActive)
//            },
//            Response.ErrorListener {
//                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
//            }
//        ){}
//        val queue: RequestQueue = Volley.newRequestQueue(this)
//        queue.add(strReq)
//    }

    fun getUser(user:String){
        val req = JSONObject()
        req.put("user",user)

        val registerRequest = JsonObjectRequest(Request.Method.POST, "${WholeShareApiService.WS_HOST}/register", req,
            { response ->
                val userJson = response.getJSONObject("user")
                val registeredUser = Gson().fromJson(userJson.toString(), User::class.java)
                userActive = User(registeredUser.id,registeredUser.username,registeredUser.password,registeredUser.full_name,registeredUser.phone,registeredUser.address,registeredUser.email,registeredUser.role,registeredUser.created_at,registeredUser.updated_at,registeredUser.deleted_at)
            },
            { error ->
                alertDialogFailed("failed!", error.toString())
            })
        // Set the timeout retry policy
        registerRequest.retryPolicy =
            DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Queue the request to the service
        WholeShareApiService.getInstance(this).addToRequestQueue(registerRequest)
    }

    //fungsi untuk berganti fragment
    fun switchFragment(angka : Int){
        if(angka==0){
            var bundle = Bundle()
            var fr = AdminHomeFragment()
            fr.arguments = bundle
            var ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container_admin, fr)
            ft.commit()
        }
        else if(angka==1){
            var bundle = Bundle()
            var fr = AdminMasterFragment()
            fr.arguments = bundle
            var ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container_admin, fr)
            ft.commit()
        }
        else if(angka==2){
            var bundle = Bundle()
            var fr = UserProfileFragment(unameActive)
            fr.arguments = bundle
            var ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container_admin, fr)
            ft.commit()
        }
        else if(angka==3){
            var bundle = Bundle()
            var fr = AdminAddLocationFragment()
            fr.arguments = bundle
            var ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container_admin, fr)
            ft.commit()
        }
    }

    //fungsi untuk ganti fragment update
    fun switchFragUpdate(id : Int)
    {
        var bundle = Bundle()
        bundle.putInt("id",id)
        var fr = AdminUpdateLocation()
        fr.arguments = bundle
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container_admin, fr)
        ft.commit()
    }

    //fungsi untuk ganti fragment detail
    fun switchFragDetail(id : Int)
    {
        var bundle = Bundle()
        bundle.putInt("id",id)
        var fr = AdminListPackageFragment()
        fr.arguments = bundle
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container_admin, fr)
        ft.commit()
    }

    //fungsi untuk make report
    fun switchMakeReport(id : Int)
    {
        var bundle = Bundle()
        bundle.putInt("id",id)
        var fr = AdminReportFragment()
        fr.arguments = bundle
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container_admin, fr)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.opt_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.item_logout){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    //alert dialog warning
    fun alertDialogFailed(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(baseContext)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    //alert dialog sukses
    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(baseContext)
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

}