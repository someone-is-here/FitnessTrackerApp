package com.example.fitnesstrackerapp.mvvm.fragments.additional

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.fitnesstrackerapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog: DialogFragment() {
    private var yesFunction: (() -> Unit)?  = null

    fun setYesListener(listener: (() -> Unit)){
        yesFunction = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(requireContext().getString(R.string.cancel_the_run))
            .setMessage(requireContext().getString(R.string.sure_cancel_run))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(requireContext().getString(R.string.yes)){ _, _ ->
                yesFunction?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton(requireContext().getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        return dialog
    }
}