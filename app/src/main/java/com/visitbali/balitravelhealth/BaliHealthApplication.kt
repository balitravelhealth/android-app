package com.visitbali.balitravelhealth

import android.app.Application
import com.visitbali.balitravelhealth.data.api.RetrofitClient

class BaliHealthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
