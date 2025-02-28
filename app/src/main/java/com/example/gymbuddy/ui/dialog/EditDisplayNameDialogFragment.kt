package com.example.gymbuddy.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.gymbuddy.R


class EditDisplayNameDialogFragment : DialogFragment() {

    interface EditUsernameDialogListener {
        fun onDisplayNameUpdated(displayName: String)
    }

    private lateinit var listener: EditUsernameDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as EditUsernameDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement EditUsernameDialogListener")
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_displayname, null)
        val editTextUsername: EditText = view.findViewById(R.id.editTextUsername)

        builder.setView(view)
            .setTitle("Edit Username")
            .setPositiveButton("Save") { _, _ ->
                val username = editTextUsername.text.toString()
                listener.onDisplayNameUpdated(username)
            }
            .setNegativeButton("Cancel", null)

        return builder.create()
    }
}