package com.example.gymbuddy.ui.chat

import ChatViewModel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
        setupCopyButton()  // New function
        return binding.root
    }

    private fun setupSendButton() {
        binding.buttonSend.setOnClickListener {
            val userMessage = binding.editTextMessage.text.toString().trim()

            if (userMessage.isNotEmpty()) {
                binding.textViewResponse.text = "GymBuddy is Thinking..." // Show loading state
                chatViewModel.sendMessage(userMessage) { response ->
                    binding.textViewResponse.text = response // Update response

                    // Show the copy button when a response is received
                    binding.buttonCopy.visibility = View.VISIBLE
                }
                binding.editTextMessage.text?.clear() // Clear input box
            } else {
                Toast.makeText(requireContext(), "Please enter a question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCopyButton() {
        binding.buttonCopy.setOnClickListener {
            val responseText = binding.textViewResponse.text.toString()

            if (responseText.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Chat Response", responseText)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(requireContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
