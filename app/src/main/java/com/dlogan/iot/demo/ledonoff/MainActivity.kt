package com.dlogan.iot.demo.ledonoff

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.dlogan.iot.demo.R
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Gpio
import java.io.IOException
import com.google.android.things.contrib.driver.button.Button




/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val TAG = "HomeActivity"
    private val BUTTON_PIN_NAME = "BCM5" //IO port wired to the Button
    private val LED_PIN_NAME = "BCM6"// IO port wired to the LED
    private var mButtonInputDriver: ButtonInputDriver? = null
    private var ledGpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "------------------------------------------------------------------------")
        Log.i(TAG, "-------------------------- Starting HomeActivity -----------------------")
        Log.i(TAG, "------------------------------------------------------------------------")

        val manager = PeripheralManager.getInstance()

        Log.i(TAG, "Available GPIO: " + manager.gpioList)

        configureButtonIo()
        configureLedIo(manager)
    }

    override fun onStart() {
        super.onStart()
        mButtonInputDriver?.register()
    }

    override fun onStop() {
        super.onStop()
        mButtonInputDriver?.unregister()
    }

    private fun configureButtonIo() {
        Log.i(TAG, "configureButtonIo()")

        try {
            mButtonInputDriver = ButtonInputDriver(
                BUTTON_PIN_NAME,
                Button.LogicState.PRESSED_WHEN_LOW,
                KeyEvent.KEYCODE_SPACE
            )
        } catch (e: IOException) {
            Log.e(TAG, "Error on configureButtonIo()", e)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Handle button pressed event
            toggleLed()
            true
        } else super.onKeyDown(keyCode, event)

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Handle button released event
            true
        } else super.onKeyUp(keyCode, event)

    }

    private fun configureLedIo(manager: PeripheralManager?) {
        Log.i(TAG, "configureLedIo()")

        try {
            // Step 1. Create GPIO connection.
            ledGpio = manager?.openGpio(LED_PIN_NAME)
            // Step 2. Configure as an output.
            ledGpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            Log.e(TAG, "Error on configureLedIo API", e)
        }

    }


    private fun toggleLed() {
        Log.e(TAG, "toggleLed() ")

        if (ledGpio == null) {
            Log.e(TAG, "toggleLedRunnable ledGpio is null")
            return
        }

        try {
            Log.i(TAG, "toggleLedRunnable  ")

            val isOn = ledGpio?.value
            // Step 3. Toggle the LED state
            if (isOn != null) {
                ledGpio?.value = !isOn
            }

            Log.i(TAG, "toggleLedRunnable   ledGpio?.value="+ledGpio?.value)

        } catch (e: IOException) {
            Log.e(TAG, "Error on toggleLedRunnable", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Close the resources
        if (ledGpio != null) {
            try {
                ledGpio?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error on PeripheralIO API", e)
            }

        }
    }
}
