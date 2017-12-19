using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UnityBluetoothAdapterCallback : MonoBehaviour
{

    public void OnMessage(string message)
    {
        Debug.Log(message);
    }
}
