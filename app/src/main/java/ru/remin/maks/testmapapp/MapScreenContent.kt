package ru.remin.maks.testmapapp

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

val ROUTE_START_LOCATION = Point(55.7559841135176, 37.775643114286176)
val ROUTE_END_LOCATION = Point(55.73750555259552, 37.78701568035127)
val SCREEN_CENTER = Point(
    (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
    (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
)

@Composable
fun MapScreenContent(mapView: MapView,) {
    mapView.map.apply {
        isRotateGesturesEnabled = true
        isScrollGesturesEnabled = true
        isZoomGesturesEnabled = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { view ->
                // The callback to be invoked after the layout is inflated.
                // Update the view if necessary
            },
        )
    }

    buildRoute(
        context = LocalContext.current,
        mapView = mapView
    )
}

fun buildRoute(
    context: Context,
    mapView: MapView
) {
    DirectionsFactory.initialize(context)

    mapView.map.move(
        CameraPosition(
            SCREEN_CENTER, 14f, 0f, 0f
        ),
        Animation(Animation.Type.SMOOTH, 1.5F),
        null
    )

    // Create a new driving router
    val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
    val mapObjects = mapView.map.mapObjects.addCollection()

    // Build the route options
    val drivingOptions = DrivingOptions()
    val vehicleOptions = VehicleOptions()

    val requestPoints = ArrayList<RequestPoint>()
    requestPoints.add(
        RequestPoint(
            ROUTE_START_LOCATION,
            RequestPointType.WAYPOINT,
            null
        )
    )
    requestPoints.add(
        RequestPoint(
            ROUTE_END_LOCATION,
            RequestPointType.WAYPOINT,
            null
        )
    )

    // Request the route from the driving router
    drivingRouter.requestRoutes(
        requestPoints,
        drivingOptions,
        vehicleOptions,
        object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                routes.forEach { route ->
                    mapObjects.addPolyline(route.geometry)
                }
            }

            override fun onDrivingRoutesError(error: Error) {
                when (error) {
                    is RemoteError -> {
                        println("RemoteError")
                    }
                    is NetworkError -> {
                        println("NetworkError")
                    }
                }
            }
        }
    )
}