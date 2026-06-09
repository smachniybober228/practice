package ci.nsu.mobile.main

import android.app.Application
import ci.nsu.mobile.main.di.ServiceLocator

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
            private set
        // Удобный доступ к ServiceLocator
        val serviceLocator: ServiceLocator by lazy {
            ServiceLocator(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Если нужно что-то инициализировать в ServiceLocator автоматически – можно вызвать serviceLocator
    }
}