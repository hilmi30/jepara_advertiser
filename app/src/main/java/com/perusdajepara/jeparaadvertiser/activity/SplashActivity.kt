package com.perusdajepara.jeparaadvertiser.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.perusdajepara.jeparaadvertiser.R
import io.paperdb.Paper
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Paper.init(this)

        val handler = Handler()
        handler.postDelayed({
            startActivity<MainActivity>()
            finish()
        }, 2000)

    }
}
