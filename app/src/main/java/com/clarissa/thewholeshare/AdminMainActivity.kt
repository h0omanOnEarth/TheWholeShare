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
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray

class AdminMainActivity : AppCompatActivity() {

    lateinit var navbar_admin : BottomNavigationView

    //semua fragment dari admin :
    lateinit var fragmentHome : AdminHomeFragment
    lateinit var fragmentMaster: AdminMasterFragment
    lateinit var fragmentAddLocation : AdminAddLocationFragment
    lateinit var fragmentListPackage : AdminListPackageFragment
    lateinit var fragmentVerifyPackage : AdminVerifyPackageFragment
    lateinit var fragmentProfile : UserProfileFragment

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
            getUserLoggedIn(usernameActive)
        }

        loadFragmentHome()
        loadFragmentAddLocation()
        loadFragmentMaster()
        loadFragmentListPackages()
        loadFragmentVerifyPackage()
        loadFragmentProfile()

        switchFragment(R.id.fragment_container_admin, fragmentHome)

        navbar_admin.setOnItemSelectedListener {

            if(it.itemId == R.id.item_home_admin){
                switchFragment(R.id.fragment_container_admin, fragmentHome)
            }else if(it.itemId == R.id.item_master_admin){
                switchFragment(R.id.fragment_container_admin, fragmentMaster)
            }else if(it.itemId == R.id.item_packages_admin){
                switchFragment(R.id.fragment_container_admin,fragmentListPackage)
            }else if(it.itemId == R.id.item_profile_admin){
                switchFragment(R.id.fragment_container_admin, fragmentProfile)
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

    //load fragment home
    fun loadFragmentHome(){
        fragmentHome = AdminHomeFragment()
    }

    //load fragment master
    fun loadFragmentMaster(){
        fragmentMaster = AdminMasterFragment()
        fragmentMaster.onClickButton = {resource: String ->
            if(resource=="add"){
                switchFragment(R.id.fragment_container_admin,fragmentAddLocation)
            }
        }
    }

    //load fragment add location
    fun loadFragmentAddLocation(){
        fragmentAddLocation = AdminAddLocationFragment()
        fragmentAddLocation.onClickButton = {resource: String ->
            if(resource=="cancel"){
                switchFragment(R.id.fragment_container_admin,fragmentMaster)
            }
        }
    }

    //load fragment list packages
    fun loadFragmentListPackages(){
        fragmentListPackage = AdminListPackageFragment()
    }

    //load fragment profile
    fun loadFragmentProfile(){
        fragmentProfile = UserProfileFragment(unameActive)
    }

    //load fragment Verify Package
    fun loadFragmentVerifyPackage(){
        fragmentVerifyPackage = AdminVerifyPackageFragment()
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