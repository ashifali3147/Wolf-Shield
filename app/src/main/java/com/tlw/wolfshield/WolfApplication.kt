package com.tlw.wolfshield

import android.app.Application
import com.tlw.wolfshield.utils.LocalData

class WolfApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        LocalData.init(this)
    }
}