package infinity.to.loop.betteryoutube.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


fun newLocationAnimator(target: View, factor: Float): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(target, "y", target.y, target.y - (target.height * factor))
    animator.duration = 300
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
