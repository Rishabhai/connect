package com.github.arekolek.phone

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
import android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import kotlinx.android.synthetic.main.activity_dialer.*
import android.content.Context;
import android.net.Uri;
import androidx.core.net.toUri;
import android.widget.Button;
import android.widget.Toast;
import android.os.Environment;




class DialerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        phoneNumberInput.setText(intent?.data?.schemeSpecificPart)
    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
        val btn_click_me = findViewById(R.id.callButton) as Button

        btn_click_me.setOnClickListener {
            // your code to perform when the user clicks on the button

            makeCall()
        }
    }

    private fun makeCall() {
        if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
//            val uri = "tel:${phoneNumberInput.text}".toUri()
//            startActivity(Intent(Intent.ACTION_CALL, uri))
            val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val uri = Uri.fromParts("tel", "${phoneNumberInput.text}", null)
            val extras = Bundle()
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
            telecomManager.placeCall(uri, extras)
        } else {
            requestPermissions(this, arrayOf(CALL_PHONE), REQUEST_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION && PERMISSION_GRANTED in grantResults) {
            makeCall()
        }
    }

    private fun offerReplacingDefaultDialer() {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            Intent(ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                .let(::startActivity)
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }
}
