package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        navbar_driver = findViewById(R.id.navbar_driver)

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
        fragmentProfile = UserProfileFragment()
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