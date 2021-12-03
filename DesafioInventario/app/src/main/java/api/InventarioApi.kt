package api

import model.Usuario
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.*

interface InventarioApi {

    @GET("/usuario/{username}")
    fun getUsuario(@Path("username") username: String): Call<Usuario>

    @Headers("Content-Type:application/json")
    @POST("/addUser")
    fun addUser(@Body info: Usuario): Call<ResponseBody>
}