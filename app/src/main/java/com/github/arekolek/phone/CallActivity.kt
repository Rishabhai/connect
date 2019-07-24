package com.github.arekolek.phone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_call.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.core.app.ActivityCompat


class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private val MY_PERMISSION = 0;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data.schemeSpecificPart
        checkAuthorizationStatus()

    }

    override fun onStart() {
        super.onStart()

        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }

        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe { finish() }
            .addTo(disposables)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {

        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
        if(state==Call.STATE_ACTIVE){
            Toast.makeText(this,"Recording Started",Toast.LENGTH_LONG).show()
            try {
                prepareRecorder()

            }catch (e:IOException){
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                Log.d("PREPARE","${e}")
            }
            startRecoring()
        }

        if(state==Call.STATE_DISCONNECTED){
            Toast.makeText(this,"Recording Completed",Toast.LENGTH_LONG).show()
            stopRecording()
        }
    }

    fun checkAuthorizationStatus() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSION)
        }
    }


    fun prepareRecorder(){
        recorder = MediaRecorder();
        recorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()

        try {
            recorder?.setOutputFile("${directory}/record.aac");
        }catch (e:IOException){
            Log.d("SETOUTPUTFILE","${e}")
        }
        recorder?.prepare();
    }

    fun startRecoring(){
        recorder?.start();
        isRecording = true;

    }

    fun stopRecording(){
        recorder?.stop()
        recorder?.release()
        isRecording = false;
    }


    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }
}
