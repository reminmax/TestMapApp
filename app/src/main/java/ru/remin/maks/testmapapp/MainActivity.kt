package ru.remin.maks.testmapapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import ru.remin.maks.testmapapp.ui.theme.TestMapAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        setContent {
            TestMapAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun MapScreen() {

    val localContext = LocalContext.current

    MapKitFactory.getInstance().onStart()

    val mapView = remember {
        MapView(localContext)
    }
    mapView.onStart()

    MapScreenContent(
        mapView = mapView
    )

    DisposableEffect(Unit) {
        onDispose {
            MapKitFactory.getInstance().onStop()
            mapView.onStop()
        }
    }
}
