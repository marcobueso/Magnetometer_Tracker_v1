package com.example.magnetometer_tracker_v1

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import kotlin.math.pow
import kotlin.math.sqrt
import java.util.*







class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorDisplay: TextView
    private lateinit var locationDisplay: TextView
    var buffer = mutableListOf(0)
    var counter = 0

    // Implemented from SensorEventListener interface
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val values = event.values
            println("------NEW VALUES READ-----")
            val magnitude = sqrt( values[0].pow(2) + values[1].pow(2) +values[2].pow(2) )
            // Add value to buffer and remove oldest value
            buffer.add(magnitude.toInt())
            buffer.removeAt(0)

            sensorDisplay.text = "reading at 0,1,2: ${values[0].toInt()}, ${values[1].toInt()}, ${values[2].toInt()}"

            if (counter % 50 == 0) {
                if (magnitude > 25 && magnitude < 40) {
                    locationDisplay.text = "INSIDE CLASSROOM"
                    locationDisplay.setBackgroundColor(Color.parseColor("green"))
                }
                else {
                    locationDisplay.text = "OUTSIDE CLASSROOM"
                    locationDisplay.setBackgroundColor(Color.parseColor("red"))
                }
            }

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

        // To avoid dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sensorDisplay = findViewById(R.id.tv_sensorDisplay)
        locationDisplay = findViewById(R.id.tv_locationDisplay)
        // Initialize buffer to 100 elements (already has one)
        for (i in 0..98) {
            buffer.add(0)
        }


        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss")
        val currentDate = sdf.format(Date())
        val filename = "user$currentDate.txt"
        var fileObj = getDir("magnet-data", Context.MODE_APPEND)

        // Take measurement button
        b_takeMeasurement.setOnClickListener {
            // Save current buffer (list[100]) to filename
            var outObj = openFileOutput(filename, Context.MODE_APPEND).use {
                it.write(buffer.toString().toByteArray())
                it.write('\n'.toInt())
            }

        }

        setUpSensors()

    }

    private fun setUpSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also{
            // register listener
            sensorManager.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_UI,
                SensorManager.SENSOR_DELAY_UI) // TODO check different delays
        }
    }



}
