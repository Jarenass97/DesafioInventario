package model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Dispositivo(
    @SerializedName("identificador")
    var id: String,

    @SerializedName("nombre")
    var nombre: String,

    @SerializedName("aula")
    var aula: String

) : Serializable
