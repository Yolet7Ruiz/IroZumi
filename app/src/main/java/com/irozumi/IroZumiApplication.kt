package com.irozumi

import android.app.Application
import com.irozumi.core.security.TokenManager
import com.irozumi.data.local.DatabaseProvider

/**
 * Clase Application para inicializar componentes globales.
 * Sigue Clean Architecture al proporcionar una única instancia de la base de datos
 * para ser inyectada o accedida por los repositorios.
 */
class IroZumiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialización temprana de la base de datos y seguridad
        DatabaseProvider.getDatabase(this)
        TokenManager.init(this)
    }
}
