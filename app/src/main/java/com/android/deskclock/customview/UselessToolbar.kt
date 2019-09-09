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

    private var leftImageView: ImageView
    private var rightImageView: ImageView
    private var titleView: TextView
    private var leftTextView: TextView
    private var rightTextView: TextView

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
            array.apply {
                title = getString(R.styleable.UselessToolbar_title)
                leftText = getString(R.styleable.UselessToolbar_left_text)
                rightText = getString(R.styleable.UselessToolbar_right_text)
                leftIcon = getResourceId(R.styleable.UselessToolbar_left_image, 0)
                rightIcon = getResourceId(R.styleable.UselessToolbar_right_image, 0)
            }
        } finally {
            array?.recycle()
        }
        toolbarView = LayoutInflater.from(context).inflate(R.layout.useless_tool_bar, this, false)
        toolbarView.apply {
            titleView = findViewById(R.id.toolbar_title)
            leftTextView = findViewById(R.id.toolbar_left_text)
            leftImageView = findViewById(R.id.toolbar_image_left)
            rightTextView = findViewById(R.id.toolbar_right_text)
            rightImageView = findViewById(R.id.toolbar_image_right)
        }
        addView(toolbarView)
        initToolbarView()
    }

    private fun initToolbarView() {
        if (title != null) {
            titleView.text = title
        }

        if (leftText != null) {
            leftTextView.text = leftText
        } else if (leftIcon != null) {
            leftImageView.apply {
                setImageResource(leftIcon)
                visibility = View.VISIBLE
            }
            leftTextView.visibility = View.GONE
        }

        if (rightText != null) {
            rightTextView.text = rightText
        } else if (rightIcon != null) {
            rightImageView.apply {
                setImageResource(rightIcon)
                visibility = View.VISIBLE
            }
            rightTextView.visibility = View.GONE
        }
    }


    fun setOnLeftItemClickListener(callback: () -> Unit) {
        if (leftTextView.visibility != View.GONE) {
            leftTextView.setOnClickListener {
                callback()
            }
        } else if (leftImageView.visibility != View.GONE) {
            leftImageView.setOnClickListener {
                callback()
            }
        }
    }

    fun setOnRightItemClickListener(callback: () -> Unit) {
        if (rightTextView.visibility != View.GONE) {
            rightTextView.setOnClickListener {
                callback()
            }
        } else if (rightImageView.visibility != View.GONE) {
            rightImageView.setOnClickListener {
                callback()
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChild(getChildAt(0), widthMeasureSpec, heightMeasureSpec)
    }

    fun setTitle(title: String) {
        toolbarView.findViewById<TextView>(R.id.toolbar_title).text = title
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getChildAt(0).layout(0, 0, getChildAt(0).measuredWidth, getChildAt(0).measuredHeight)
    }
}