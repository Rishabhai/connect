package com.github.arekolek.phone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
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
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.core.app.ActivityCompat
import java.util.Date
import android.media.AudioManager
import android.telecom.InCallService
import com.github.arekolek.phone.OngoingCall.call


class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private val MY_PERMISSION = 0;
    private var customerList: HashSet<String> = HashSet(10)
    var audioManager: AudioManager? = null
    var inCallService: InCallService? = null
    private var muteStatus :Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        checkAuthorizationStatus()
        number = intent.data.schemeSpecificPart

//        audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
//        audioManager!!.setMode(AudioManager.MODE_IN_CALL);


    }

    override fun onStart() {
        super.onStart()

        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }

        holdBtn.setOnClickListener {
            if(OngoingCall.state.value==Call.STATE_ACTIVE){
                OngoingCall.hold()
            }
            else if(OngoingCall.state.value==Call.STATE_HOLDING) {
                OngoingCall.unHold()
            }
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
        if(state == Call.STATE_HOLDING){
            holdBtn.text = "UNHOLD"
        }else{
            holdBtn.text = "HOLD"
        }

        customerCallHandler(number) // if not customer then close app

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )

        holdBtn.isVisible = state in listOf(
            Call.STATE_HOLDING,
            Call.STATE_ACTIVE
        )

        recordingHandler(state)
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
        fun startCall(context:Context){
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call!!.details.handle)
                .let(context::startActivity)
        }

    }

    private fun customerCallHandler(number:String){

//        if(!customerList.contains(number)){
//            this.finish();//replace with better implementation to pass InCallService to default app
//            System.exit(0);
//        }

    }

    private fun recordingHandler(state:Int){
        if(state==Call.STATE_ACTIVE && !isRecording){
            try {
                prepareRecorder()
                startRecording()
                isRecording=true
            }catch (e:IOException){
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                Log.d("PREPARE","${e}")
            }

        }

        if(state==Call.STATE_DISCONNECTED && isRecording){
            Toast.makeText(this,"Recording Completed",Toast.LENGTH_SHORT).show()
            stopRecording()
        }
    }

    private fun checkAuthorizationStatus() {
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

    private fun prepareRecorder(){
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        var directory = this.getFilesDir()

        var name = "${number}_${Date()}"
        Toast.makeText(this,name,Toast.LENGTH_LONG).show()
        try {
            recorder?.setOutputFile("${directory}/${name}.aac");
        }catch (e:IOException){
            Log.d("SETOUTPUTFILE","${e}")
        }
        recorder?.prepare();
    }

    private fun startRecording(){
        recorder?.start();
        isRecording = true;

    }

    private fun stopRecording(){
        recorder?.stop()
        recorder?.release()
        isRecording = false;
    }


    private fun mute(value:Boolean){
        try{
            inCallService!!.setMuted(value)
        }catch (e:IOException){
            Toast.makeText(this,"${e}",Toast.LENGTH_LONG)
        }
    }
}
