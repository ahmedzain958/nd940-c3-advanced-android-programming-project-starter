package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0


    private var buttonColor = 0
    private var loadingButtonColor = 0
    private var circleColor = 0
    private var textColor = 0
    private var buttonText = ""
    private var loadingText = ""


    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingButtonColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            buttonText = getString(R.styleable.LoadingButton_buttonText).toString()
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()
        }
        isClickable = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw button background
        buttonPaint.color = buttonColor
        canvas.drawRect(0f, height.toFloat() / 2, width.toFloat(), 350f, buttonPaint)



       /* textPaint.color = textColor
        canvas.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            ((height + 180) / 2).toFloat(),
            textPaint
        )*/
        drawText(canvas, widthSize.toFloat() / 2, ((height + 180) / 2).toFloat(), textPaint)
    }

    private fun drawText(canvas: Canvas, x: Float, y: Float, textPaint: Paint) {
        textPaint.color = textColor
        canvas.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            ((height + 180) / 2).toFloat(),
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}