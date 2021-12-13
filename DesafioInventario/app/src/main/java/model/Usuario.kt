package model

import android.graphics.Bitmap
import assistant.Auxiliar
import assistant.Rol
import com.example.desafioinventario.LoginActivity
import com.example.desafioinventario.R
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
    var img: ByteArray? = byteArrayOf()
) : Serializable {
    fun isJefe(): Boolean {
        return roles.contains(Rol.JEFE_DEPARTAMENTO)
    }

    fun sinImagen(): Boolean {
        return img.contentEquals(byteArrayOf())
    }
}