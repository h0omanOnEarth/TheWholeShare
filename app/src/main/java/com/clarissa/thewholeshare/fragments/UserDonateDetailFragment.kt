package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.helpers.ParticipantsStatuses
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class UserDonateDetailFragment(
    var status : Participant,
    var arrRequests: MutableList<Request>,
    var arrNews : MutableList<News>
) : Fragment() {

    var onClickButton:((resource:String)->Unit)? = null
    lateinit var btnBack : Button
    lateinit var tvTo : TextView
    lateinit var tvFrom : TextView
    lateinit var tvStatus : TextView
    lateinit var tvDate : TextView
    lateinit var imgView : ImageView
    lateinit var tvId : TextView
    lateinit var btnToReport : Button
    lateinit var tvExp : TextView
    lateinit var tvnote : TextView

    var onClickButtonReport:((resource:String,news: News)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_donate_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack = view.findViewById(R.id.btnverif)
        tvTo = view.findViewById(R.id.tvTo_detailDonateUser)
        tvFrom = view.findViewById(R.id.tvFrom_detailUserDonate)
        tvStatus = view.findViewById(R.id.tvStatus_detailUserDonate)
        tvDate = view.findViewById(R.id.tvDate_detailUserDonate)
        imgView = view.findViewById(R.id.imgView_detailUserDonate)
        tvId = view.findViewById(R.id.tvId_detailDonateUser)
        btnToReport = view.findViewById(R.id.btnToReport_detailUserDonate)
        tvExp = view.findViewById(R.id.tvExp_detailUserDonate)
        tvnote = view.findViewById(R.id.tvNote)

        println("News" + arrNews.size)

        val statusParticipant = status.status
        var statusRequest = ""
        for(i in arrRequests.indices){
            if(arrRequests[i].id==status.request_id){
                statusRequest =  arrRequests[i].status
                break
            }
        }

        //image view & status
        if(statusParticipant == ParticipantsStatuses.PENDING){
            tvStatus.text = "Pending"
            imgView.setImageResource(R.drawable.time_machine_120px)
        }else if(statusParticipant == ParticipantsStatuses.DELIVERING){
            tvStatus.text = "On The Way"
            imgView.setImageResource(R.drawable.on_the_way)
        }else if(statusParticipant == ParticipantsStatuses.DELIVERED || statusParticipant == ParticipantsStatuses.VERIFIED){
            tvStatus.text = if (statusParticipant == ParticipantsStatuses.DELIVERED) "Delivered" else "Verified"
//            imgView.setImageResource(R.drawable.ok_120px)
            Picasso.with(requireContext()).load("${WholeShareApiService.WS_STORAGE_IMAGE}/courier_delivered_${status.id}.png").into(imgView)
        }else if(statusParticipant == ParticipantsStatuses.CANCELLED){
            tvStatus.text = "Canceled"
            imgView.setImageResource(R.drawable.cancel_120px)
        }

        if(statusRequest=="Finished"){
            btnToReport.isEnabled = true
        }else{
            btnToReport.isEnabled = false
        }

        tvId.text = "#" + status.id

        tvFrom.text = status.pickup
        tvDate.text = status.created_at.toString().substring(0,10)

        //cetak notes
        tvnote.text = status.note
        tvnote.movementMethod = ScrollingMovementMethod()

        for(i in arrRequests.indices){
            if(arrRequests[i].id == status.request_id){
                tvTo.text = arrRequests[i].location
                tvExp.text = arrRequests[i].deadline.substring(0,10)
                break
            }
        }

        btnBack.setOnClickListener {
            onClickButton?.invoke("back")
        }


        btnToReport.setOnClickListener {

            var news : News = News(-1,"","",-1)

            for(i in arrNews.indices){
                if(arrNews[i].request_id==status.request_id){
                    news = arrNews[i]
                    break
                }
            }

            onClickButtonReport?.invoke("report",news)
        }
    }
}