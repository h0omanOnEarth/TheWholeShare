package com.clarissa.thewholeshare

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    // Untuk user yang sedang login
    lateinit var userActive : User
    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        // Initialize UI components
        navbar_driver = findViewById(R.id.navbar_driver)

        // Initialize Variables
        userActive = intent.getSerializableExtra("active_user") as User // Get the logged user

        switchFragment(R.id.fragment_container_driver, DriverHomeFragment(), Bundle())

        // Add navigation event
        navbar_driver.setOnItemSelectedListener {
            // Switches the fragment based on what button is clicked
            when (it.itemId) {
                R.id.item_home_driver ->
                    switchFragment(R.id.fragment_container_driver, DriverHomeFragment(), Bundle())
                R.id.item_packages_driver ->
                    switchFragment(R.id.fragment_container_driver, DriverPackagesFragment(), Bundle())
                R.id.item_delivered_driver ->
                    switchFragment(R.id.fragment_container_driver, DriverListDeliverFragment(), Bundle())
                R.id.item_canceled_driver ->
                    switchFragment(R.id.fragment_container_driver, DriverListCanceledFragment(), Bundle())
                R.id.item_profile_driver ->
                    switchFragment(R.id.fragment_container_driver, UserProfileFragment(userActive.username), Bundle())
            }
            return@setOnItemSelectedListener true
        }
    }

    //fungsi untuk berganti fragment
    public fun switchFragment(containerViewId:Int, fragment: Fragment, bundle: Bundle){
        bundle.putSerializable("active_user", userActive)
        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(containerViewId, fragment)
        fragmentManager.commit()

        activeFragment = fragment
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

//    private fun openCameraInterface(fragment: Fragment) {
//        val content = ContentValues()
//        content.put(MediaStore.Images.Media.TITLE, "WholeSharePhoto")
//        content.put(MediaStore.Images.Media.DESCRIPTION, "A report photo for WholeShare")
//
//        imageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
//
//
//    }
}