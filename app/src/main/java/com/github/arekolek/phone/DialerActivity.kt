package com.github.arekolek.phone

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.annotation.SuppressLint
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
import android.content.pm.PackageManager
import android.net.Uri;
import android.telecom.Call
import android.widget.Button;
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class DialerActivity : AppCompatActivity() {

    private val MY_PERMISSION = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        phoneNumberInput.setText(intent?.data?.schemeSpecificPart)
    }

    override fun onStart() {
        super.onStart()

        onGoingCallHandler()

        offerReplacingDefaultDialer()
        val btn_click_me = findViewById(R.id.callButton) as Button

        btn_click_me.setOnClickListener {
            makeCall()
        }

        var files = this.fileList()
        if(files.isEmpty()){
            recordings.text = "No Recordings Yet"
        }else{
            var fileNames = files.count().toString()

            for(i in files){
                fileNames= "${fileNames} \n ${i}"
            }
            recordings.text = fileNames
        }
    }



    private fun makeCall() {
        if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
            val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val uri = Uri.fromParts("tel", "${phoneNumberInput.text}", null)
            val extras = Bundle()
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

    private fun onGoingCallHandler(){
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE
                ),
                MY_PERMISSION)
        }
        val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        if (telecomManager.isInCall()){
            Toast.makeText(this,"CALL Goiing ON",Toast.LENGTH_SHORT).show()
            CallActivity.startCall(this)
        }else{
            Toast.makeText(this," N0 ONgoing Call",Toast.LENGTH_SHORT).show()
        }
    }
}
