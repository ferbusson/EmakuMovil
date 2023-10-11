package com.example.emakumovil

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.Ostermiller.util.MD5
import com.example.emakumovil.communications.PackageToXML
import com.example.emakumovil.communications.SocketConnector
import com.example.emakumovil.communications.SocketWriter
import com.example.emakumovil.components.ProgressDialogManager
import com.example.emakumovil.misc.parameters.EmakuParametersStructure
import com.example.emakumovil.misc.settings.ConfigFileHandler
import com.example.emakumovil.ui.theme.EmakuMovilTheme
import java.io.IOException
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException
import java.security.NoSuchAlgorithmException


class MainActivity : ComponentActivity() {

    private var ETempresa: EditText? = null
    private var ETusuario: EditText? = null
    private var ETpassword: EditText? = null
    private var Bingresar: Button? = null
    private var packageXML: PackageToXML? = null

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }


        //Bingresar = (Button) findViewById(R.id.ingresar);
        //ETempresa = (EditText) findViewById(R.id.empresa);
        //ETusuario = (EditText) findViewById(R.id.login);
        //ETpassword = (EditText) findViewById(R.id.password);

        setContent {
            EmakuMovilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(isOnline(applicationContext)) {
                        //Greeting(name = "We are online ;) !")
                            ConfigFileHandler.loadSettings()
                    }
                    else {
                        Greeting(name = ":/ We are offline")
                    }

                    setContentView(R.layout.activity_list_item);
                    Bingresar = (Button(findViewById(R.id.ingresar)))
                    ETempresa = (EditText(findViewById(R.id.empresa)))
                    ETusuario = (EditText(findViewById(R.id.login)))
                    ETpassword = (EditText(findViewById(R.id.password)))

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun onClick(v: View?) {
    val empresa: String = ETempresa.getText().toString()
    val usuario: String = ETusuario.getText().toString()
    val password: String = ETpassword.getText().toString()
    if (empresa == "" || usuario == "" || password == "") {
        Toast.makeText(
            this@MainActivity,
            "Error: Uno de los campos requeridos esta vacio",
            Toast.LENGTH_LONG
        ).show()
    } else {
        conect(empresa, usuario, password)
        ProgressDialogManager.show(
            this, MainActivity.handler,
            getString(R.string.cargando)
        )
    }
}

private fun saveInfoConnection(
    empresa: String, usuario: String,
    password: String
) {
    val connections: SharedPreferences = getSharedPreferences(
        "lastConnectionEmaku", Context.MODE_PRIVATE
    )
    val editor = connections.edit()
    editor.putString("empresa", empresa)
    editor.putString("usuario", usuario)
    editor.putString("passwd", password)
    editor.commit()
}

private fun conect(empresa: String, usuario: String, password: String) {
    val socketConnector: SocketConnector
    Bingresar.setEnabled(false)
    ConfigFileHandler.setCurrentCompany(empresa)
    try {
        socketConnector = SocketConnector(
            ConfigFileHandler.getHost(),
            ConfigFileHandler.getServerPort(), packageXML
        )
        socketConnector.start()
        EmakuParametersStructure.removeParameter("dataBase")
        EmakuParametersStructure.addParameter("dataBase", empresa)
        EmakuParametersStructure.removeParameter("userLogin")
        EmakuParametersStructure.addParameter("userLogin", usuario)
        val socket = SocketConnector.getSock()
        val md5 = MD5(password)
        SocketConnector.setDatabase(empresa)
        SocketConnector.setUser(usuario)
        SocketConnector.setPassword(md5.getDigest())
        saveInfoConnection(empresa, usuario, SocketConnector.getPassword())
        SocketWriter.writing(
            socket,
            CNXSender.getPackage(
                empresa, usuario,
                SocketConnector.getPassword()
            )
        )
    } catch (CEe: ConnectException) {
        Log.d(
            "EMAKU", "EMAKU direccion:" + ConfigFileHandler.getHost()
                    + " puerto " + ConfigFileHandler.getServerPort()
                    + " empresa " + empresa + " usuario " + usuario
                    + " password " + password
        )
        Toast.makeText(
            this@MainActivity,
            "Error: Revise la empresa usuario o contraseña",
            Toast.LENGTH_LONG
        ).show()
        Bingresar.setEnabled(true)
    } catch (e: UnresolvedAddressException) {
        Toast.makeText(
            this@MainActivity,
            "Error: No se puede resolver la direccion "
                    + ConfigFileHandler.getHost() + " puerto "
                    + ConfigFileHandler.getServerPort(),
            Toast.LENGTH_LONG
        ).show()
        Bingresar.setEnabled(true)
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
}

fun enableButton() {
    Bingresar.setEnabled(true)
}

val handler: Handler = object : Handler() {
    fun handleMessage(msg: Message) {
        val total: Int = msg.getData().getInt("total")
        ProgressDialogManager.currentDialog().progress = total
        if (total <= 0) {
            try {
                ProgressDialogManager.dismissCurrent()
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}
}