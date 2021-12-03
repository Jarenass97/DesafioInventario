package model

import android.graphics.Bitmap
import assistant.Rol
import java.io.Serializable
import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("username")
    var username: String,
    @SerializedName("password")
    var passwd: String,
    @SerializedName("roles")
    var roles: ArrayList<Rol>,
    @SerializedName("email")
    var email: String,
    @SerializedName("image")
    var img: Bitmap? = null
) : Serializable {
}