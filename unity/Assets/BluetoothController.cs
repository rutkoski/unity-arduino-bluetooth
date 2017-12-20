using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BluetoothController : MonoBehaviour
{

    //public UnityBluetoothAdapter adapter;

    public ArduinoBluetoothAdapter adapter;

    public void ListDevices()
    {
        try
        {
            //string[] devices = adapter.GetBoundDeviceNames();

            //Debug.Log(devices.ToString());

            adapter.init("00:21:13:01:CA:9F");
        }
        catch (Exception e)
        {
            //
        }
    }

    public void Connect()
    {
        try
        {
            //adapter.Connect("Gear VR Controller(913F)");
            adapter.connect();
            adapter.write("<who>");
        }
        catch (Exception e)
        {
            //
        }
    }
}
