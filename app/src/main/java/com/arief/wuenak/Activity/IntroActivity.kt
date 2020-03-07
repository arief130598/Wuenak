package com.arief.wuenak.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.arief.wuenak.Fragment.IntroSlider1
import com.arief.wuenak.Fragment.IntroSlider2
import com.arief.wuenak.Fragment.IntroSlider3
import com.arief.wuenak.R
import com.arief.wuenak.SharedPreference.IntroPreference
import com.github.paolorotolo.appintro.AppIntro2

class IntroActivity : AppIntro2() {

    private lateinit var manager: IntroPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager = IntroPreference(this)
        if(manager.isFirstRun()){
            setIntro()
        }else{
            finishIntro()
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finishIntro()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finishIntro()
    }

    private fun setIntro(){
        addSlide(IntroSlider1())
        addSlide(IntroSlider2())
        addSlide(IntroSlider3())

        setDepthAnimation()
        showSkipButton(false)
    }

    private fun finishIntro(){
        val goLogin = Intent(this, LoginActivity::class.java)
        startActivity(goLogin)
    }
}
