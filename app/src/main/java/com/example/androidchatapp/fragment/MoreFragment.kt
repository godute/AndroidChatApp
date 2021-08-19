package com.example.androidchatapp.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.androidchatapp.databinding.FragmentMoreBinding
import com.example.androidchatapp.services.FirebaseAuthService

private const val TAG = "MoreFragment"
class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            moreFragment = this@MoreFragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun signOut() {
        Log.d(TAG, "signOut Called")

        FirebaseAuthService.signOut()
        activity?.finishAndRemoveTask()
    }
}