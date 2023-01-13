package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.api.WholeShareApiService
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        // Initialize UI components
        navbar_driver = findViewById(R.id.navbar_driver)

        // Initialize Variables
        userActive = intent.getSerializableExtra("active_user") as User // Get the logged user

        // Load the fragments for this activity
        loadFragments()

        switchFragment(R.id.fragment_container_driver, fragmentHome)

        // Add navigation event
        navbar_driver.setOnItemSelectedListener {
            // Switches the fragment based on what button is clicked
            when (it.itemId) {
                R.id.item_home_driver ->
                    switchFragment(R.id.fragment_container_driver, fragmentHome)
                R.id.item_packages_driver ->
                    switchFragment(R.id.fragment_container_driver, fragmentListAvailablePackages)
                R.id.item_delivered_driver ->
                    switchFragment(R.id.fragment_container_driver, fragmentListDeliverPackages)
                R.id.item_canceled_driver ->
                    switchFragment(R.id.fragment_container_driver, fragmentListCanceledPackages)
                R.id.item_profile_driver ->
                    switchFragment(R.id.fragment_container_driver, fragmentProfile)
            }
            return@setOnItemSelectedListener true
        }
    }

    /**
     * Loads the fragments needed for this activity.
     */
    fun loadFragments() {
        fragmentHome = DriverHomeFragment()
        fragmentListAvailablePackages = DriverPackagesFragment()
        fragmentListDeliverPackages = DriverListDeliverFragment()
        fragmentListCanceledPackages = DriverListCanceledFragment()
        fragmentProfile = UserProfileFragment(userActive.username)
//        fragmentDetailAvailablePackage = DriverPackageDetailFragment()
        fragmentDetailDeliverPackage = DriverDeliveredPackageDetailFragment()
        fragmentDetailCanceledPackage = DriverCanceledPageDetailFragment()
        fragmentFinishPackage = DriverFinishDetailFragment()
    }

    //fungsi untuk berganti fragment
    fun switchFragment(containerViewId:Int, fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("active_user", userActive)
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