package com.perusdajepara.jeparaadvertiser.utils

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.Handler
import io.paperdb.Paper

class DelayServices: IntentService("DelayServices") {

    override fun onHandleIntent(intent: Intent?) {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val handler = Handler()
        handler.postDelayed({
            Paper.book().write(Constant.DELAY_STATS, false)
        }, 15000)
        return Service.START_STICKY
    }
}