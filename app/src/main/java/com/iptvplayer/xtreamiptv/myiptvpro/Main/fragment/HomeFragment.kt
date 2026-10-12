package com.iptvplayer.xtreamiptv.myiptvpro.Main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.iptvplayer.xtreamiptv.myiptvpro.R

class HomeFragment : Fragment() {

    private var pageTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageTitle = it.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_page_title).text = pageTitle
        
        view.findViewById<View>(R.id.search_bar).setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            startActivity(intent)
        }
    }

    companion object {
        private const val ARG_TITLE = "title"

        @JvmStatic
        fun newInstance(title: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
    }
}
