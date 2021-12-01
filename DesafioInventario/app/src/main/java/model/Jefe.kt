package model

import android.graphics.Bitmap

class Jefe(id: Int, username: String, passwd: String, rol: Rol, img: Bitmap? = null) :
    Usuario(
        id, username, passwd,
        rol = Rol.JEFE_DEPARTAMENTO, img
    ) {
}
