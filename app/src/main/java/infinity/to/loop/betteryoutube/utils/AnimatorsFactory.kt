package infinity.to.loop.betteryoutube.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator

fun newLocationAnimator(target: View, factor: Float): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(target, "y", target.y, target.y - (target.height * factor))
    animator.duration = 200
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun scaleAnimatorX(target: View, factor: Float): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(target, "scaleX", target.scaleX, target.scaleX * factor)
    animator.duration = 300
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun scaleAnimatorY(target: View, factor: Float): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(target, "scaleY", target.scaleY, target.scaleY * factor)
    animator.duration = 300
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun heightAnimator(endValue: Int, startValue: Int = 0): ValueAnimator {
    val animator = ValueAnimator.ofInt(startValue, endValue)
    animator.duration = 500
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun fadeAnimation(target: View): ViewPropertyAnimator {
    val animation = target.animate().alpha(1f)
    animation.duration = 300
    animation.startDelay = 250
    animation.interpolator = AccelerateDecelerateInterpolator()
    return animation
}
