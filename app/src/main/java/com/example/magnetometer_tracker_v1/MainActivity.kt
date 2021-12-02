package com.example.magnetometer_tracker_v1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorDisplay: TextView

    // Implemented from SensorEventListener interface
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val values = event.values
            for (item in values){
                println("ITEM: $item")
            }

            sensorDisplay.text = "reading at 0,1,2: ${values[0].toInt()}, ${values[1].toInt()}, ${values[2].toInt()}"

        }
    }

    // Implemented from SensorEventListener interface
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // To avoid
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sensorDisplay = findViewById(R.id.tv_sensorDisplay)

        setUpSensors()
    }

    private fun setUpSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also{
            // register listener
            sensorManager.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST) // TODO check different delays
        }
    }

}
