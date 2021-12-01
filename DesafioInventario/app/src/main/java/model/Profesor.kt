package model

import android.graphics.Bitmap

class Profesor(id: Int, username: String, passwd: String, rol: Rol, img: Bitmap? = null) :
    Usuario(
        id, username, passwd,
        rol = Rol.PROFESOR, img
    ) {
}