package com.android.deskclock.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.deskclock.R

class UselessToolbar : ViewGroup {

    private val title: String?
    private val leftText: String?
    private val rightText: String?
    private val leftIcon: Int?
    private val rightIcon: Int?
    private val toolbarView: View

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        var array: TypedArray? = null
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.UselessToolbar)
            title = array?.getString(R.styleable.UselessToolbar_title)
            leftText = array?.getString(R.styleable.UselessToolbar_left_text)
            rightText = array?.getString(R.styleable.UselessToolbar_right_text)
            leftIcon = array?.getResourceId(R.styleable.UselessToolbar_left_image, 0)
            rightIcon = array?.getResourceId(R.styleable.UselessToolbar_right_image, 0)
        } finally {
            array?.recycle()
        }
        toolbarView = LayoutInflater.from(context).inflate(R.layout.useless_tool_bar, this)
        initToolbarView()
    }

    private fun initToolbarView() {
        if (title != null) {
            toolbarView.findViewById<TextView>(R.id.toolbar_title).text = title
        }

        if (leftText != null) {
            toolbarView.findViewById<TextView>(R.id.toolbar_left_text).text = leftText
        } else if (leftIcon != null) {
            toolbarView.findViewById<ImageView>(R.id.toolbar_image_left).setImageResource(leftIcon)
        }

        if (rightText != null) {
            toolbarView.findViewById<TextView>(R.id.toolbar_right_text).text = leftText
        } else if (rightIcon != null) {
            toolbarView.findViewById<ImageView>(R.id.toolbar_image_right)
                .setImageResource(rightIcon)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        toolbarView.layout(0, 0, toolbarView.width, toolbarView.height)
    }
}