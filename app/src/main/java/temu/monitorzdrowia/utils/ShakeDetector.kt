package temu.monitorzdrowia.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Klasa detekcji zatrzęsienia urządzenia (Shake Detector) wykorzystująca akcelerometr.
 *
 * @param onShake Funkcja callback wywoływana po wykryciu zatrzęsienia.
 */

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private val shakeThreshold = 6f// Czułość detekcji
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private val shakeTimeout = 1000

    fun start(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.also { accel ->
            sensorManager?.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Oblicz przyspieszenie całkowite
        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        // Suma kwadratów przyspieszeń
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > shakeThreshold) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > shakeTimeout) {
                lastShakeTime = currentTime
                shakeCount++
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nie używane
    }
}
