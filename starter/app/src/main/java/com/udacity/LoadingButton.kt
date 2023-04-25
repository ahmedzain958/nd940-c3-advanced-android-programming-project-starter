package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
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
    private var loadingProgress = 0f

    private var isClicked= false

    private var widthR= 0f

    private var valueAnimator = ValueAnimator()
    private var beginAngle=0f
    private var endAngle=0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> {
                invalidate()
                state(ButtonState.Loading)
                buttonText = resources.getString(R.string.button_loading)
                isClicked = true
            }
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.download)

                startButtonAnimation()
                myCircle()
            }
            ButtonState.Completed -> {
                buttonPaint.color = buttonColor
                buttonText = loadingText
                isClicked = false
                invalidate()
            }
        }
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
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
        drawText(canvas, widthSize.toFloat() / 2, ((height + 180) / 2).toFloat(), textPaint)
        if (isClicked){
            buttonPaint.color = loadingButtonColor
            canvas.drawRect(0f, height.toFloat() / 2, widthR.toFloat(), measuredHeight.toFloat(), buttonPaint)
            buttonPaint.color = Color.YELLOW
            canvas.drawArc((widthSize - 180f), (heightSize / 2) - 50f, (widthSize - 100f), (heightSize / 2) + 50f,
                beginAngle, endAngle, true, buttonPaint
            )
        }
    }

    private fun startButtonAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0F, measuredWidth.toFloat()).apply {
            addUpdateListener { animate ->
                animate.repeatMode = ValueAnimator.REVERSE
                animate.interpolator = AccelerateInterpolator()
                animate.repeatCount = ValueAnimator.INFINITE
                widthR = animate.animatedValue as Float
                invalidate()
            }
            duration = 2300
            start()
        }
    }
    private fun myCircle(){
        valueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener { animate ->
                animate.interpolator = AccelerateInterpolator()
                endAngle = animate.animatedValue as Float
                invalidate()
            }
            duration = 2300
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    custom_button.isEnabled = false
                }
                override fun onAnimationEnd(animation: Animator?) {
                    custom_button.isEnabled = true
                    state(ButtonState.Completed)
                }
            })
        }
    }
    fun state(State: ButtonState){
        buttonState = State
    }
    private fun drawCircle(canvas: Canvas) {
        val circleLeft = (widthSize.toFloat() + textPaint.measureText(buttonText)) / 2 + heightSize.toFloat() * 0.1f
        val circleTop = heightSize.toFloat() * 0.3f
        val circleRight = circleLeft + heightSize.toFloat() * 0.4f
        val circleBottom = heightSize.toFloat() - heightSize.toFloat() * 0.3f

        circlePaint.color = circleColor
        canvas.drawArc(
            circleLeft,
            circleTop,
            circleRight,
            circleBottom,
            0F,
            360F * loadingProgress,
            true,
            circlePaint
        )
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