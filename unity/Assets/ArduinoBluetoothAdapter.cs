using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ArduinoBluetoothAdapter : MonoBehaviour
{

    public string address;

    private const string JAVA_CLASS = "com.bluetooth.rutkoski.unitybluetoothadapter.ArduinoBluetoothAdapter";

    public static ArduinoBluetoothAdapter I;
    AndroidJavaObject ajo;

    protected virtual void Awake()
    {
        I = this;
    }

    protected virtual void Start()
    {
        if (ajo == null)
            ajo = new AndroidJavaObject(JAVA_CLASS, address);
    }
    
    public virtual void Connect()
    {
        ajo.Call("Connect");
    }

    public virtual void Disconnect()
    {
        ajo.Call("Disconnect");
    }

    public virtual void Send(string message)
    {
        ajo.Call("Send", message);
    }

    public virtual void OnConnected()
    {
        //
    }

    public virtual void OnDisconnected()
    {
        //
    }

    public virtual void OnMessageReceived(string message)
    {
        Debug.Log(message);
    }

    public virtual void OnError(string message)
    {
        Debug.Log(message);
    }
}
