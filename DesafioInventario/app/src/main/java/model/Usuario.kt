package model

import android.graphics.Bitmap
import java.io.Serializable

abstract class Usuario(
    var id: Int,
    var username: String,
    var passwd: String,
    var rol: Rol,
    var img: Bitmap? = null
):Serializable {
}