package com.capacitorjs.plugins.googlemaps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.Marker

class CapacitorClusterManagerRenderer(
    private val appContext: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<CapacitorGoogleMapMarker>?,
    minClusterSize: Int?
) : DefaultClusterRenderer<CapacitorGoogleMapMarker>(appContext, map, clusterManager) {

    init {
        if(minClusterSize != null && minClusterSize > 0) {
            super.setMinClusterSize(minClusterSize)
        }
    }

    override fun onBeforeClusterItemRendered(item: CapacitorGoogleMapMarker, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        item.markerOptions?.let {
            markerOptions.position(it.position)
            markerOptions.title(it.title)
            markerOptions.snippet(it.snippet)
            markerOptions.alpha(it.alpha)
            markerOptions.flat(it.isFlat)
            markerOptions.draggable(it.isDraggable)
            if(null != it.icon) {
                markerOptions.icon(it.icon)
            }
        }
    }

   override fun onBeforeClusterRendered(cluster: Cluster<CapacitorGoogleMapMarker>, markerOptions: MarkerOptions) {
       val bitmap = createCustomClusterIcon(appContext, cluster.size)
       markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
   }

    override fun shouldRenderAsCluster(cluster: Cluster<CapacitorGoogleMapMarker>): Boolean {
        return cluster.size > 1;
    }

    override fun onClusterRendered(
        cluster: Cluster<CapacitorGoogleMapMarker>,
        marker: Marker
    ) {
        super.onClusterRendered(cluster, marker)
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomClusterIcon(appContext, cluster.size)))
    }

    private fun createCustomClusterIcon(context: Context, clusterSize: Int): Bitmap {
        return try {
            val inputStream = context.assets.open("cluster_pin.png")
            val baseImage = BitmapFactory.decodeStream(inputStream)
                ?: throw Exception("Failed to decode cluster_pin.png")

            val scaleFactor = 0.38f
            val newWidth = (baseImage.width * scaleFactor).toInt()
            val newHeight = (baseImage.height * scaleFactor).toInt()

            val scaledImage = Bitmap.createScaledBitmap(baseImage, newWidth, newHeight, true)

            val bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(scaledImage, 0f, 0f, null)

            val paint = Paint().apply {
                color = Color.parseColor("#4A5468") // Your custom text color
                textSize = 32f // Adjust for smaller icons
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            val x = newWidth / 2f
            val y = newHeight / 2f - ((paint.descent() + paint.ascent()) / 2)
            canvas.drawText(clusterSize.toString(), x, y, paint)

            bitmap
        } catch (e: Exception) {
            Log.d("ClusterRenderer", "Rendering cluster err " + e.message.toString())
            Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        }
    }

}
