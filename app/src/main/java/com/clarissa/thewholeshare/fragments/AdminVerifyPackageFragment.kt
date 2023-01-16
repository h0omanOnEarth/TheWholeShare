package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Participant
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.w3c.dom.Text
import java.util.HashMap

class AdminVerifyPackageFragment : Fragment() {

    lateinit var tvto : TextView
    lateinit var tvnote : TextView
    lateinit var tvstatus : TextView
    lateinit var tvdate : TextView
    lateinit var tvsender : TextView
    lateinit var tvfrom : TextView
    lateinit var tvverif : TextView
    lateinit var btnverif : Button
    lateinit var btncancel : Button
    lateinit var btnback : Button
    lateinit var tvid : TextView
    lateinit var imageViewPhoto: ImageView

    var arrPackage = ArrayList<Participant>()

    var idpackage = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idpackage = arguments?.getInt("id",-1)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_admin_verify_package, container, false)
        tvto = view.findViewById(R.id.tvTo_detailDonateUser)
        tvnote = view.findViewById(R.id.tvnote)
        tvstatus = view.findViewById(R.id.tvStatus_detailUserDonate)
        tvdate = view.findViewById(R.id.tvDate_detailUserDonate)
        tvsender = view.findViewById(R.id.tvSender_admin_verify)
        tvfrom = view.findViewById(R.id.tvFrom_admin_verify)
        tvverif = view.findViewById(R.id.tvVerifiedAt_admin_verify)
        btnverif = view.findViewById(R.id.btnverif)
        btnback = view.findViewById(R.id.btnback)
        btncancel = view.findViewById(R.id.btncancel_admin)
        tvid = view.findViewById(R.id.tvId_detailDonateUser)
        imageViewPhoto = view.findViewById(R.id.imgView_detailUserDonate)


        getData()
        btnback.setOnClickListener(View.OnClickListener {
            (context as AdminMainActivity).switchFragment(1)
        })
        btnverif.setOnClickListener(View.OnClickListener {
            updateStatus(3)
            (context as AdminMainActivity).switchFragment(1)
        })
        btncancel.setOnClickListener(View.OnClickListener {
            updateStatus(4)
            (context as AdminMainActivity).switchFragment(1)
        })

        return view
    }

    fun getData() {
        tvid.setText("#"+idpackage)
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/getPackageByID",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                for (i in 0 until obj.length()) {
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val user_id = o.getInt("user_id")
                    val request_id = o.getInt("request_id")
                    val courier_id = o.getInt("courier_id")
                    val fullname = o.getString("full_name")
                    val pickup = o.getString("pickup")
                    val note = o.get("note").toString()
                    val status = o.getInt("status")
                    val location = o.getString("location")
                    val created_at = o.getString("created_at")
                    isiData(location,note,status,created_at,fullname,pickup,"")
                    val r = Participant(
                        id, user_id, request_id, courier_id, pickup, note, status, fullname,location,created_at
                    )
                    arrPackage.add(r)
                }

            },
            Response.ErrorListener {
                println(it.message)
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = idpackage.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    fun isiData(to : String,note : String, status : Int, date : String, sender : String, from: String, verif:String){
        tvto.setText(to)
        tvnote.setText(note)
        if(status==2){
            tvstatus.setText("Delivered")
            btnverif.isEnabled=true
            btncancel.isEnabled=true
        }
        else if(status==3){
            tvstatus.setText("Verified")
            btnverif.isEnabled=false
            btncancel.isEnabled=true

        }
        else if(status==4){
            tvstatus.setText("Cancelled")
            btncancel.isEnabled=false
            btnverif.isEnabled=true
        }
        tvdate.setText(date)
        tvsender.setText(sender)
        tvfrom.setText(from)
        tvverif.setText(verif)

        Log.d("NAMA_FILE", "isiData: courier_delivered_${idpackage}.png")
        Picasso.with(requireContext()).load("${WholeShareApiService.WS_STORAGE_IMAGE}/courier_delivered_${idpackage}.png").into(imageViewPhoto)
    }

    fun updateStatus(stat : Int){
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/updateStatusParticipants",
            Response.Listener {
            },
            Response.ErrorListener {
//                println(it.message)
//                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = idpackage.toString()
                params["status"] = stat.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }
}