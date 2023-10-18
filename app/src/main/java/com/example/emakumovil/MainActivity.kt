package com.example.emakumovil

import android.content.Context
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
import com.example.emakumovil.communications.PackageToXML
import com.example.emakumovil.control.HeadersValidator
import com.example.emakumovil.misc.settings.ConfigFileHandler
import com.example.emakumovil.misc.settings.ConfigFileNotLoadException
import com.example.emakumovil.ui.theme.EmakuMovilTheme


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT > 9) {
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

                    setContentView(R.layout.activity_main);

                    Bingresar = findViewById(R.id.ingresar);
                    ETempresa =  findViewById(R.id.empresa);
                    ETusuario =  findViewById(R.id.login);
                    ETpassword = findViewById(R.id.password);

                    if (isOnline(applicationContext)){
                        NotificationText("We are Online!")
                    try {
                        ConfigFileHandler.loadSettings()
                        Log.d("EMAKU", "Archivo cargado")
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
        //Bingresar!!.setOnClickListener(this)
        packageXML = PackageToXML()
        val headers = HeadersValidator(this)
        packageXML!!.addArrivePackageListener(headers)
    }


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