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
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.fragments.*
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray

class UserMainActivity : AppCompatActivity() {

    lateinit var navbar_user:BottomNavigationView


    //semua fragment dari user :
    lateinit var fragmentHome : HomeUserFragment
    lateinit var fragmentDonate : UserDonateFragment
    lateinit var fragmentListStatusDonate : UserListStatusFragment
    lateinit var fragmentDetailStatus : UserDonateDetailFragment
    lateinit var fragmentProfile : UserProfileFragment
    lateinit var fragmentDetailNews : UserDetailNewsFragment
    lateinit var fragmentReportUser : UserDonateReportFragment

    lateinit var arrRequests : MutableList<Request>
    lateinit var arrNews : MutableList<News>
    lateinit var arrParticipants : MutableList<Participant>


    //untuk user yang sedang login
    lateinit var userActive : User
    var unameActive = ""
    var id_active = -1

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        navbar_user = findViewById(R.id.navbar_user)

        userActive = User(-1,"","","","","","",-1,"")

        arrRequests = mutableListOf()
        arrNews = mutableListOf()
        arrParticipants = mutableListOf()


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
                            id_active = u.id
                            break
                        }
                    }
                    println(userActive)
                    println(id_active)
                },
                Response.ErrorListener {
                    Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
                }
            ){}
            val queue: RequestQueue = Volley.newRequestQueue(this)
            queue.add(strReq)
        }

        //ambil data dari intent
        val usernameActive = intent.getStringExtra("active_username")
        if(usernameActive!=null){
            unameActive = usernameActive
            getUserLoggedIn(usernameActive)
        }

        fetchRequests()
        fetchNews()
        fetchParticipants()


        //load semua fragment terlebih dahulu :
        loadFragmentHome()
        loadFragmentDonate()
        loadListStatusDonate()
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

    //fetch data requests
    fun fetchRequests(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequest",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrRequests.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val location = o.getString("location")
                    val batch = o.getInt("batch")
                    val deadline = o.get("deadline").toString()
                    val note = o.getString("note")
                    val status = o.getInt("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val req = Request(
                        id,location,batch,deadline,note,status,created_at,updated_at,deleted_at
                    )
                    arrRequests.add(req)
                }
                println(arrRequests.size)
            },
            Response.ErrorListener {
                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //fetch data news
    fun fetchNews(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listNews",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrNews.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val title = o.getString("title")
                    val content = o.getString("content")
                    val request_id = o.getInt("request_id")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val news = News(
                        id,title,content,request_id,created_at,updated_at,deleted_at
                    )
                    arrNews.add(news)
                }
                println(arrNews.size)
            },
            Response.ErrorListener {
                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //fetch data participants
    fun fetchParticipants(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listParticipants",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrParticipants.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val user_id = o.getInt("user_id")
                    val request_id = o.getInt("request_id")
                    val pickup = o.getString("pickup")
                    val status = o.getInt("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()

                    val participant = Participant(
                        id,user_id,request_id,pickup,status,created_at,updated_at
                    )

                    if(user_id==userActive.id){
                        arrParticipants.add(participant)
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //load fragment detail news
    fun loadFragmentDetailNews(news:News){
        fragmentDetailNews = UserDetailNewsFragment(news)
        fragmentDetailNews.onClickButton = {resource: String->
            if(resource == "back"){
                switchFragment(R.id.fragment_container_user,fragmentHome)
            }
        }
    }

    //load fragment home nya user
    fun loadFragmentHome(){
        fragmentHome = HomeUserFragment(arrRequests,arrNews)
        fragmentHome.onClickButton = {resource: String,news:News ->
            if(resource == "edit"){
                loadFragmentDetailNews(news)
                switchFragment(R.id.fragment_container_user,fragmentDetailNews)
            }
        }
    }

    //load fragment donasi nya user
    fun loadFragmentDonate(){
        fragmentDonate = UserDonateFragment(unameActive,arrParticipants,arrRequests,arrNews)
        fragmentDonate.onClickButton = {resource: String ->
            if(resource=="donate"){
                loadFragmentHome()
                switchFragment(R.id.fragment_container_user,fragmentDonate)
            }
        }
    }

    //load fragment list status
    fun loadListStatusDonate(){
        fragmentListStatusDonate = UserListStatusFragment(unameActive,arrParticipants,arrRequests)
        fragmentListStatusDonate.onClickButton = {resource: String, status: Participant ->
            if(resource=="detail") {
                loadDetailStatusDonate(status)
                switchFragment(R.id.fragment_container_user, fragmentDetailStatus)
            }
        }
    }

    fun loadFragmentReportDetail(news: News){
        fragmentReportUser = UserDonateReportFragment(news)
        fragmentReportUser.onClickButton = {resource: String ->
            if(resource=="back"){
                switchFragment(R.id.fragment_container_user,fragmentDetailStatus)
            }
        }
    }

    //load fragment detail status
    fun loadDetailStatusDonate(status:Participant){
        fragmentDetailStatus = UserDonateDetailFragment(status)
        fragmentDetailStatus.onClickButton = {resource: String ->
            if(resource=="back"){
                switchFragment(R.id.fragment_container_user, fragmentListStatusDonate)
            }
        }
        fragmentDetailStatus.onClickButtonReport = {resource: String, news: News ->
            if(resource=="report"){
                loadFragmentReportDetail(news)
                switchFragment(R.id.fragment_container_user,fragmentReportUser)
            }
        }
    }

    //load fragment profile
    fun loadFragmentProfile(){
        fragmentProfile = UserProfileFragment(unameActive)
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