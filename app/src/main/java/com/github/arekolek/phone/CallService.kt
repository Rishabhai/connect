package com.github.arekolek.phone

import android.telecom.Call
import android.telecom.InCallService
import android.widget.Toast

class CallService : InCallService() {
    private var isOnCall = false
    private var currentCall: Call? = null
    private var callsInBetweenCallService = arrayOf<Call>();

    override fun onCallAdded(call: Call) {

        Toast.makeText(this,OngoingCall.call?.details.toString(),Toast.LENGTH_SHORT).show()

        if(OngoingCall.call==null && !isOnCall){
            isOnCall=true
            OngoingCall.call = call
            currentCall = call
            CallActivity.start(this, call)
            //Log Call Start Time

        }else{
            OngoingCall.call = currentCall
            callsInBetweenCallService.plusElement(call)
            //Log calls in between a call
        }

    }


    override fun onCallRemoved(call: Call) {
        OngoingCall.call = null
        isOnCall=false

        callsInBetweenCallService = arrayOf()
        //Log Call End Time
    }
}