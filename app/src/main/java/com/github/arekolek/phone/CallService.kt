package com.github.arekolek.phone

import android.telecom.Call
import android.telecom.InCallService
import android.widget.Toast

class CallService : InCallService() {

    override fun onCallAdded(call: Call) {
        OngoingCall.call = call
        CallActivity.start(this, call)

    }

    override fun onCallRemoved(call: Call) {
        OngoingCall.call = null
        Toast.makeText(this,"onCallRemoved ", Toast.LENGTH_LONG).show()
    }
}