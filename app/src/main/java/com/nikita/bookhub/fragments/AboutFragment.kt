package com.nikita.bookhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.nikita.bookhub.R
import android.view.LayoutInflater as LayoutInflater1

class AboutFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater1, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }
}


