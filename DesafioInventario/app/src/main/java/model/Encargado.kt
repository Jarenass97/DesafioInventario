package model

import android.graphics.Bitmap

class Encargado(id: Int, username: String, passwd: String, rol: Rol, img: Bitmap? = null) :
    Usuario(
        id, username, passwd,
        rol = Rol.ENCARGADO, img
    ) {
}