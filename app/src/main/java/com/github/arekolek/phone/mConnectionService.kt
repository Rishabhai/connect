package com.github.arekolek.phone

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.widget.Toast

class mConnectionService : ConnectionService() {

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this,"onCreate ConnectionService got Called",Toast.LENGTH_SHORT).show()

    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request)
        Toast.makeText(this,"onCreateOutgoingConnection got Called",Toast.LENGTH_LONG).show()
        return Connection.createFailedConnection(null)
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Toast.makeText(this,"onCreateOutgoingConnectionFailed got Called",Toast.LENGTH_LONG).show()
//        val connection = new mConnection();
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request)
        Toast.makeText(this,"onCreateIncomingConnectionFailed got Called",Toast.LENGTH_LONG).show()

    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Toast.makeText(this,"onCreateInComingConnectionFailed got Called",Toast.LENGTH_LONG).show()

    }

}
