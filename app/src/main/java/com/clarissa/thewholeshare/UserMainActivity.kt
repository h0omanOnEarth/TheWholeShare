package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserMainActivity : AppCompatActivity() {

    lateinit var navbar_user:BottomNavigationView


    //semua fragment dari user :
    lateinit var fragmentHome : HomeUserFragment
    lateinit var fragmentDonate : UserDonateFragment
    lateinit var fragmentListStatusDonate : UserListStatusFragment
    lateinit var fragmentDetailStatus : UserDonateDetailFragment
    lateinit var fragmentProfile : UserProfileFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        navbar_user = findViewById(R.id.navbar_user)

        //load semua fragment terlebih dahulu :
        loadFragmentHome()
        loadFragmentDonate()
        loadListStatusDonate()
        loadDetailStatuDonate()
        loadFragmentProfile()

        //karena pertama yang diload adalah home maka : home terlebih dahulu
        switchFragment(R.id.fragment_container_user,fragmentHome)

        navbar_user.setOnItemSelectedListener {

            if(it.itemId == R.id.item_home_user){
                switchFragment(R.id.fragment_container_user,fragmentHome)
            }else if(it.itemId== R.id.item_donate_user){
                switchFragment(R.id.fragment_container_user,fragmentDonate)
            }else if(it.itemId == R.id.item_status_user){
                switchFragment(R.id.fragment_container_user,fragmentListStatusDonate)
            }else if(it.itemId== R.id.item_profile_user){
                switchFragment(R.id.fragment_container_user, fragmentProfile)
            }

            return@setOnItemSelectedListener true
        }
    }

    //load fragment home nya user
    fun loadFragmentHome(){
        fragmentHome = HomeUserFragment()
    }

    //load fragment donasi nya user
    fun loadFragmentDonate(){
        fragmentDonate = UserDonateFragment()
    }

    //load fragment list status
    fun loadListStatusDonate(){
        fragmentListStatusDonate = UserListStatusFragment()
    }

    //load fragment detail status
    fun loadDetailStatuDonate(){
        fragmentDetailStatus = UserDonateDetailFragment()
    }

    //load fragment profile
    fun loadFragmentProfile(){
        fragmentProfile = UserProfileFragment()
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