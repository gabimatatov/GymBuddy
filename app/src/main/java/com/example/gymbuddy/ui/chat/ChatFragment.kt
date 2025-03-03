package com.example.gymbuddy.ui.chat

import ChatViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gymbuddy.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        setupSendButton()
        return binding.root
    }

    private fun setupSendButton() {
        binding.buttonSend.setOnClickListener {
            val userMessage = binding.editTextMessage.text.toString().trim()

            if (userMessage.isNotEmpty()) {
                binding.textViewResponse.text = "GymBuddy is Thinking..." // Show loading state
                chatViewModel.sendMessage(userMessage) { response ->
                    binding.textViewResponse.text = response // Update response
                }
                binding.editTextMessage.text?.clear() // Clear input box
            } else {
                Toast.makeText(requireContext(), "Please enter a question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
