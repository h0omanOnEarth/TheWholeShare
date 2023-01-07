package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.adapters.NewsAdapter
import com.clarissa.thewholeshare.adapters.StatusAdapter
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import org.json.JSONArray

class UserListStatusFragment(
    var username:String,
    var arrParticipants: MutableList<Participant>,
    var arrRequests:MutableList<Request>
) : Fragment() {

    lateinit var userActive : User
    lateinit var rvListStatus_User:RecyclerView

    lateinit var statusAdapter: StatusAdapter

    var onClickButton:((resource:String,status:Participant)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActive = User(-1,"","","","","","",-1,"")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvListStatus_User = view.findViewById(R.id.rvListStatus_User)
        rvListStatus_User.layoutManager = LinearLayoutManager(view.context.applicationContext)

        //to get user who logged in
        fun getUserLoggedIn(uname:String){
            val strReq = object: StringRequest(
                Method.GET,
                "${WholeShareApiService.WS_HOST}/listUsers",
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
                    Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
                }
            ){}
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }


        statusAdapter =  StatusAdapter(view.context,arrParticipants,arrRequests,R.layout.item_user_status)
        rvListStatus_User.adapter = statusAdapter

        getUserLoggedIn(username)
        fetchRequests()

        statusAdapter.onClick = object:StatusAdapter.clickListener{
            override fun onClick(status: Participant) {
                onClickButton?.invoke("detail",status)
            }
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
                    statusAdapter.notifyDataSetChanged()
                }
                println(arrRequests.size)
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fungsi untuk mengubah status participants yang pending menjadi canceled karena expired



}