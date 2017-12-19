using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BluetoothController : MonoBehaviour
{

    public UnityBluetoothAdapter adapter;

    public void ListDevices()
    {
        try
        {
            string[] devices = adapter.GetBoundDeviceNames();

            Debug.Log(devices.ToString());
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
            adapter.Connect("Gear VR Controller(913F)");
        }
        catch (Exception e)
        {
            //
        }
    }
}
