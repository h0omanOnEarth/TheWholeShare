package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminMainActivity : AppCompatActivity() {

    lateinit var navbar_admin : BottomNavigationView

    //semua fragment dari admin :
    lateinit var fragmentHome : AdminHomeFragment
    lateinit var fragmentMaster: AdminMasterFragment
    lateinit var fragmentAddLocation : AdminAddLocationFragment
    lateinit var fragmentListPackage : AdminListPackageFragment
    lateinit var fragmentVerifyPackage : AdminVerifyPackageFragment
    lateinit var fragmentProfile : UserProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        navbar_admin = findViewById(R.id.navbar_admin)

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

    //load fragment home
    fun loadFragmentHome(){
        fragmentHome = AdminHomeFragment()
    }

    //load fragment master
    fun loadFragmentMaster(){
        fragmentMaster = AdminMasterFragment()
    }

    //load fragment add location
    fun loadFragmentAddLocation(){
        fragmentAddLocation = AdminAddLocationFragment()
    }

    //load fragment list packages
    fun loadFragmentListPackages(){
        fragmentListPackage = AdminListPackageFragment()
    }

    //load fragment profile
    fun loadFragmentProfile(){
        fragmentProfile = UserProfileFragment()
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