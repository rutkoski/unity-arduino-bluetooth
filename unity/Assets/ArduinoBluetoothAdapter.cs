using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ArduinoBluetoothAdapter : MonoBehaviour
{

    private const string JAVA_CLASS = "com.bluetooth.rutkoski.unitybluetoothadapter.ArduinoBluetoothAdapter";

    public static ArduinoBluetoothAdapter I;
    AndroidJavaObject ajo;

    void Awake()
    {
        I = this;
    }

    void Start()
    {
        if (ajo == null)
            ajo = new AndroidJavaObject(JAVA_CLASS);
    }

    public void init(string address)
    {
        ajo.Call("init", address);
    }

    public void connect()
    {
        ajo.Call("connect");
    }

    public void disconnect()
    {
        ajo.Call("disconnect");
    }

    public void write(string message)
    {
        ajo.Call("write", message);
    }

    public void OnMsgReceived(string message)
    {
        Debug.Log(message);
    }

    public void OnError(string message)
    {
        Debug.Log(message);
    }
}
