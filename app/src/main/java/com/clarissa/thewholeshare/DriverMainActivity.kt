package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.fragments.*
import com.clarissa.thewholeshare.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray

class DriverMainActivity : AppCompatActivity() {

    lateinit var navbar_driver : BottomNavigationView

    //inisialisasi semua fragment dari driver
    lateinit var fragmentHome : DriverHomeFragment
    lateinit var fragmentListAvailablePackages : DriverPackagesFragment
    lateinit var fragmentListDeliverPackages : DriverListDeliverFragment
    lateinit var fragmentListCanceledPackages : DriverListCanceledFragment
    lateinit var fragmentProfile : UserProfileFragment
    //fragments detail :
    lateinit var fragmentDetailAvailablePackage : DriverPackageDetailFragment
    lateinit var fragmentDetailDeliverPackage : DriverDeliveredPackageDetailFragment
    lateinit var fragmentDetailCanceledPackage : DriverCanceledPageDetailFragment
    lateinit var fragmentFinishPackage : DriverFinishDetailFragment

    //untuk user yang sedang login
    lateinit var userActive : User
    var unameActive = ""

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        navbar_driver = findViewById(R.id.navbar_driver)

        userActive = User(-1,"","","","","","",-1,"")

        //ambil data dari intent
        val usernameActive = intent.getStringExtra("active_username")
        if(usernameActive!=null){
            unameActive = usernameActive
            getUserLoggedIn(usernameActive)
        }

        //load semua fragment yang ada
        loadFragmentHome()
        loadFragmentProfile()
        loadFragmentListAvailablePackages()
        loadFragmentListCanceledPackages()
        loadFragmentListDeliverPackages()
        loadFragmentDetailAvailablePackage()
        loadFragmentDetailDeliverPackage()
        loadFragmentDetailCanceledPackage()
        loadFragmentToFinishPackage()

        switchFragment(R.id.fragment_container_driver,fragmentHome)

        navbar_driver.setOnItemSelectedListener {

            if(it.itemId == R.id.item_home_driver){
                switchFragment(R.id.fragment_container_driver,fragmentHome)
            }else if(it.itemId == R.id.item_packages_driver){
                switchFragment(R.id.fragment_container_driver,fragmentListAvailablePackages)
            }else if(it.itemId == R.id.item_delivered_driver){
                switchFragment(R.id.fragment_container_driver,fragmentListDeliverPackages)
            }else if(it.itemId == R.id.item_canceled_driver){
                switchFragment(R.id.fragment_container_driver,fragmentListCanceledPackages)
            }else if(it.itemId == R.id.item_profile_driver){
                switchFragment(R.id.fragment_container_driver,fragmentProfile)
            }

            return@setOnItemSelectedListener true
        }

    }

    //to get user who logged in
    fun getUserLoggedIn(uname:String){
        val strReq = object: StringRequest(
            Method.GET,
            "$WS_HOST/listUsers",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val username = o.getString("username")
                    val password = o.getString("password")
                    val full_name = o.getString("full_name")
                    val phone = o.getString("phone")
                    val address = o.getString("address")
                    val email = o.getString("email")
                    val role = o.getInt("role")
                    val deleted_at = o.get("deleted_at").toString()
                    val u = User(
                        id,username,password,full_name,phone,address,email,role,deleted_at
                    )
                    if(username==uname) {
                        userActive = u
                        break
                    }
                }
                println(userActive)
            },
            Response.ErrorListener {
                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //load semua fragments :
    //load fragment home :
    fun loadFragmentHome(){
        fragmentHome = DriverHomeFragment()
    }

    //load fragment list packages yang available :
    fun loadFragmentListAvailablePackages(){
        fragmentListAvailablePackages = DriverPackagesFragment()
    }

    //load fragment list packages yang sedang diantar :
    fun loadFragmentListDeliverPackages(){
        fragmentListDeliverPackages = DriverListDeliverFragment()
    }

    //load fragment list packages yang telah di cancel :
    fun loadFragmentListCanceledPackages(){
        fragmentListCanceledPackages = DriverListCanceledFragment()
    }

    //load fragment ke halaman profile :
    fun loadFragmentProfile(){
        fragmentProfile = UserProfileFragment(unameActive)
    }

    //load detail page dari paket yang sedang tersedia yang dipilih :
    fun loadFragmentDetailAvailablePackage(){
        fragmentDetailAvailablePackage = DriverPackageDetailFragment()
    }

    //load detail page dari paket yang sedang diantarkan :
    fun loadFragmentDetailDeliverPackage(){
        fragmentDetailDeliverPackage = DriverDeliveredPackageDetailFragment()
    }

    //load detail page dari paket yang di cancel :
    fun loadFragmentDetailCanceledPackage(){
        fragmentDetailCanceledPackage = DriverCanceledPageDetailFragment()
    }

    //load fragment untuk menyelesaikan sebuah kiriman
    fun loadFragmentToFinishPackage(){
        fragmentFinishPackage = DriverFinishDetailFragment()
    }

    //fungsi untuk berganti fragment
    fun switchFragment(containerViewId:Int,fragment: Fragment){
        val bundle = Bundle()
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(containerViewId, fragment)
        fragmentManager.commit()
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
}