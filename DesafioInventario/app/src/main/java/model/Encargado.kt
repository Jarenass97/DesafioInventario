package model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Encargado(
    @SerializedName("id_user")
    val nombre: String
) : Serializable
