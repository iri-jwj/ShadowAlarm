package com.android.deskclock.addeditpage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.deskclock.R
import com.android.deskclock.customview.UselessToolbar


class EditLabelFragment(private val label: String) : Fragment() {

    companion object {
        private lateinit var instance: EditLabelFragment
        private lateinit var mManager: FragmentManager
        private lateinit var labelChangedCallback: (String) -> Unit
        private lateinit var oldLabel: String
        fun setUpFragment(
            manager: FragmentManager, container: Int, label: String,
            callback: (String) -> Unit
        ) {
            oldLabel = label
            instance = EditLabelFragment(label)
            mManager = manager
            labelChangedCallback = callback
            val transaction = mManager.beginTransaction()
                .setCustomAnimations(R.anim.fragmen_slide_in, 0)
            transaction.add(container, instance, AddEditAct.editLabelTag).commit()
        }

        private fun hideSelf(result: String) {
            if (result != "" && oldLabel != result) {
                labelChangedCallback(result)
            }
            val imm =
                instance.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(instance.view?.applicationWindowToken, 0)
            }
            mManager.beginTransaction().setCustomAnimations(0,R.anim.fragment_slide_out).remove(instance).commit()
        }
    }

    private var resultLabel = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_label, container, false)
        val editText = root.findViewById<EditText>(R.id.edit_label)
        val clearButton = root.findViewById<ImageView>(R.id.edit_label_clear)

        editText.apply {
            setText(label)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s == null || s.toString() == "") {
                        clearButton.visibility = View.GONE
                    } else {
                        resultLabel = s.toString()
                        clearButton.visibility = View.VISIBLE
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //do nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //do nothing
                }
            })

        }

        clearButton.setOnClickListener {
            editText.setText("")
        }

        root.findViewById<UselessToolbar>(R.id.toolbar).setOnLeftItemClickListener {
            if (resultLabel != editText.text.toString()) {
                resultLabel = editText.text.toString()
            }
            hideSelf(resultLabel)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 26) {
            view?.findViewById<EditText>(R.id.edit_label)?.apply {
                focusable = View.FOCUSABLE
                requestFocus()
            }
            val inputManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.showSoftInput(view?.findViewById<EditText>(R.id.edit_label), 0)
        }

    }
}