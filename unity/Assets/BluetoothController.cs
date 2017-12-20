using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class BluetoothController : ArduinoBluetoothAdapter
{

    // "00:21:13:01:CA:9F"

    public delegate void OnArduinoFoundEvent();
    public delegate void OnArduinoConnectedEvent();
    public delegate void OnArduinoDisconnectedEvent();

    public OnArduinoFoundEvent OnArduinoFound = delegate { };
    public OnArduinoConnectedEvent OnArduinoConnected = delegate { };
    public OnArduinoDisconnectedEvent OnArduinoDisconnected = delegate { };

    public Text responseText;

    public override void OnConnected()
    {
        base.OnConnected();

        Send("<who>");
    }
    
    public override void OnMessageReceived(string message)
    {
        base.OnMessageReceived(message);

        ShowMessage(message);

        if (message == "arduino")
        {
            OnArduinoFound();

            Send("<connect>");
        }
        else if (message == "connected")
        {
            OnArduinoConnected();
        }
        else if (message == "disconnected")
        {
            OnArduinoDisconnected();
        }
    }

    public override void OnError(string message)
    {
        base.OnError(message);

        ShowMessage(message);
    }

    private void ShowMessage(string message)
    {
        responseText.text = message;
    }
}
