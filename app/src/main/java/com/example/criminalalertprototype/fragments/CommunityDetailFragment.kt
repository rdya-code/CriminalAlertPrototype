package com.example.criminalalertprototype.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.criminalalertprototype.R
import com.example.criminalalertprototype.models.AlertModel

class CommunityDetailFragment : Fragment() {

    companion object {
        private const val ARG_ALERT = "alert_data"

        // F2: Bundle - Pass custom object from RecyclerView to Detail Fragment
        fun newInstance(alert: AlertModel): CommunityDetailFragment {
            val fragment = CommunityDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_ALERT, alert)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var alert: AlertModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // F2: Receive data via Bundle
        alert = arguments?.getParcelable(ARG_ALERT)
            ?: throw IllegalArgumentException("Alert data required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.detail_title).text = alert.title
        view.findViewById<TextView>(R.id.detail_description).text = alert.description
        view.findViewById<TextView>(R.id.detail_type).text = alert.type
        view.findViewById<TextView>(R.id.detail_time).text = alert.timeAgo
        view.findViewById<TextView>(R.id.detail_location).text = alert.location
        view.findViewById<TextView>(R.id.detail_urgency).text = "Urgency: ${alert.urgency}"

        // Set urgency color
        when (alert.urgency) {
            "High" -> view.findViewById<TextView>(R.id.detail_urgency).setTextColor(resources.getColor(R.color.alert_red))
            "Medium" -> view.findViewById<TextView>(R.id.detail_urgency).setTextColor(resources.getColor(R.color.warning_orange))
            else -> view.findViewById<TextView>(R.id.detail_urgency).setTextColor(resources.getColor(R.color.safety_blue))
        }

        view.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            Toast.makeText(context, "You confirmed this alert", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            Toast.makeText(context, "You dismissed this alert", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }
}