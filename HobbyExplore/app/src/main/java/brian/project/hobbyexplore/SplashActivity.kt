package brian.project.hobbyexplore

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)

        // RippleDrawable with center ripple effect
        val rippleColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))  // Assuming you have defined an orange color in colors.xml
        val rippleDrawable = RippleDrawable(rippleColor, null, null)
        logoImageView.background = rippleDrawable

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()  // Finish the SplashActivity so it's removed from the activity stack
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        logoImageView.startAnimation(scaleAnimation)
    }
}

