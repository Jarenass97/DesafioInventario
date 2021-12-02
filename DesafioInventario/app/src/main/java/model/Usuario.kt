package model

import android.graphics.Bitmap
import assistant.Rol
import java.io.Serializable

data class Usuario(
    var username: String,
    var passwd: String,
    var rol: Rol,
    var img: Bitmap? = null
):Serializable {
}