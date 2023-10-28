package com.example.emakumovil

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emakumovil.communications.CNXSender
import com.example.emakumovil.communications.PackageToXML
import com.example.emakumovil.communications.SocketConnector
import com.example.emakumovil.communications.SocketWriter
import com.example.emakumovil.control.HeadersValidator
import com.example.emakumovil.misc.crypto.MD5
import com.example.emakumovil.misc.parameters.EmakuParametersStructure
import com.example.emakumovil.misc.settings.ConfigFileHandler
import com.example.emakumovil.misc.settings.ConfigFileNotLoadException
import com.example.emakumovil.ui.theme.EmakuMovilTheme
import java.io.IOException
import java.net.ConnectException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.nio.channels.UnresolvedAddressException
import java.security.NoSuchAlgorithmException


class MainActivity : androidx.activity.ComponentActivity() {

    private var ETempresa: EditText? = null
    private var ETusuario: EditText? = null
    private var ETpassword: EditText? = null
    private var Bingresar: Button? = null
    private var packageXML: PackageToXML? = null

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        return false
    }

    fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    fun getMac(): String {
        val wifiManager =
            applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.macAddress
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT > 24) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        setContent {
            EmakuMovilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                    /*
                     * CLAVES TEMPORALES
                     */
                    /*ETempresa?.setText("coccoa")
                    ETusuario?.setText("emaku")
                    ETpassword?.setText("perla2010")*/

                    setContentView(R.layout.activity_main)

                    Bingresar = findViewById(R.id.ingresar)
                    ETempresa =  findViewById(R.id.empresa)
                    ETusuario =  findViewById(R.id.login)
                    ETpassword = findViewById(R.id.password)

                    if (isOnline(applicationContext)){
                        NotificationText("We are Online!")
                    try {
                        ConfigFileHandler.loadSettings()
                        System.out.println("Archivo cargado")
                        Log.d("EMAKU", "Archivo cargado")

                        //Bingresar!!.setOnClickListener( this)
                        System.out.println("Entrando a packageXML...")
                        packageXML = PackageToXML()
                        System.out.println("...sali de packageXML")
                        System.out.println("Entrando a HeadersValidator...")
                        val headers = HeadersValidator(this)
                        System.out.println("...sali de HeadersValidator")
                        System.out.println("Entrando a addArrivePackageListener...")
                        packageXML!!.addArrivePackageListener(headers)
                        System.out.println("...saliendo de addArrivePackageListener")

                    } catch (e: ConfigFileNotLoadException) {
                        // TODO Auto-generated catch block
                        Log.d("EMAKU", "No se pudo cargar el archivo de empresas")
                    }
                }
                    else {
                        NotificationText("No network connection!")
                        Toast.makeText(this, R.string.actualizar, Toast.LENGTH_LONG).show();
                        //Bingresar.isActivated(false);
                    }
                }
            }
        }

    }

    fun onClick(v: View?) {
        val empresa = ETempresa!!.text.toString()
        val usuario = ETusuario!!.text.toString()
        val password = ETpassword!!.text.toString()
        if (empresa == "" || usuario == "" || password == "") {
            Toast.makeText(
                this@MainActivity,
                "Error: Uno de los campos requeridos esta vacio",
                Toast.LENGTH_LONG
            ).show()
        } else {
            conect(empresa, usuario, password)
            /*ProgressDialogManager.show(
                this, MainActivity.handler,
                getString(android.R.string.cargando)
            )*/
        }
    }

    private fun saveInfoConnection(
        empresa: String,
        usuario: String,
        password: String,
        ip: String,
        mac: String
    ) {
        Global.setSystem_user(usuario);
        val connections = getSharedPreferences(
            "lastConnectionEmaku", MODE_PRIVATE
        )
        val editor = connections.edit()
        editor.putString("empresa", empresa)
        editor.putString("usuario", usuario)
        editor.putString("passwd", password)
        editor.putString("ip",ip)
        editor.putString("mac",mac)
        editor.commit()
    }

    private fun conect(empresa: String, usuario: String, password: String) {
        val socketConnector: SocketConnector
        Bingresar!!.isEnabled = false
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
            SocketConnector.setIp(getIpv4HostAddress())
            SocketConnector.setMac(getMac())

            saveInfoConnection(empresa, usuario, SocketConnector.getPassword(),getIpv4HostAddress(),getMac())
            SocketWriter.writing(
                socket,
                CNXSender.getPackage(
                    empresa,
                    usuario,
                    SocketConnector.getPassword(),
                    SocketConnector.getIp(),
                    SocketConnector.getMac()
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
            Bingresar!!.isEnabled = true
        } catch (e: UnresolvedAddressException) {
            Toast.makeText(
                this@MainActivity,
                "Error: No se puede resolver la direccion "
                        + ConfigFileHandler.getHost() + " puerto "
                        + ConfigFileHandler.getServerPort(),
                Toast.LENGTH_LONG
            ).show()
            Bingresar!!.isEnabled = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun enableButton() {
        Bingresar!!.isEnabled = true
    }

    /*val handler: Handler = object : Handler() {
        fun handleMessage(msg: Notification.MessagingStyle.Message) {
            val total: Int = msg.getData().getInt("total")
            ProgressDialogManager.currentDialog().progress = total
            if (total <= 0) {
                try {
                    ProgressDialogManager.dismissCurrent()
                } catch (e: IllegalArgumentException) {
                }
            }
        }
    }*/

}



@Composable
fun NotificationText(name: String, modifier: Modifier = Modifier) {
    Surface(color = Color.Cyan) {
        Text(
            text = "Hi! Bringing back $name!",
            modifier = modifier.padding(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationTextPreview() {
    EmakuMovilTheme {
        NotificationText("Emaku Mobile")
    }

}