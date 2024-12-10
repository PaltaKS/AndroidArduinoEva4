package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };
    private static final String TAG = "BluetoothExample";
    private static final String DEVICE_NAME = "HC-06"; // Nombre del dispositivo HC-06
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para la comunicación Bluetooth serial

    //Botones tienda Luces
    private Button BtnTiendaIn, BtnTiendaOff;
    //Botones Segundo Piso Luces
    private Button BtnSegPisoIn, BtnSegPisoOff;
    //Botones Garage Luces
    private Button BtnGarageIn, BtnGarageOff;
    //Botones Piso Izquierdo Luces
    private Button BtnLeftIn, BtnLeftOff;
    //Botones Piso Derecho Luces
    private Button BtnRightIn, BtnRightOff;
    //Puerta Garage
    private Button BtnGarageOpen, BtnGarageClose;
    //Apagar Todas las Luces y Cerrar Garage
    private Button BtnAllOff;





    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Botones tienda Luces
        BtnTiendaIn = findViewById(R.id.BtnTiendaIn);
        BtnTiendaOff = findViewById(R.id.BtnTiendaOff);
        //Botones Segundo Piso Luces
        BtnSegPisoIn = findViewById(R.id.BtnSegPisoIn);
        BtnSegPisoOff = findViewById(R.id.BtnSegPisoOff);
        //Botones Garage Luces
        BtnGarageIn = findViewById(R.id.BtnGarageIn);
        BtnGarageOff = findViewById(R.id.BtnGarageOff);
        //Botones Piso Izquierdo Luces
        BtnLeftIn = findViewById(R.id.BtnLeftIn);
        BtnLeftOff = findViewById(R.id.BtnLeftOff);
        //Botones Piso Derecho Luces
        BtnRightIn = findViewById(R.id.BtnRightIn);
        BtnRightOff = findViewById(R.id.BtnRightOff);
        //Puerta Garage
        BtnGarageOpen = findViewById(R.id.BtnGarageOpen);
        BtnGarageClose = findViewById(R.id.BtnGarageClose);
        //Apagar Todas las Luces y Cerrar Garage
        BtnAllOff = findViewById(R.id.BtnAllOff);



        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        // Comprobar si los permisos ya están concedidos
        if (!checkPermissions()) {
            // Los permisos no están concedidos, solicitarlos
            requestPermissions();
        } else {
            // Los permisos ya están concedidos, continuar con la lógica de la aplicación
            // ...
        }
        //conectarBluetooth();
    }

    //Botones tienda Luces Encender
    public void BtnTiendaIn(View view) {
        enviarMensaje("10");
    }
    //Botones tienda Luces Apagar
    public void BtnTiendaOff(View view) {
        enviarMensaje("11");
    }
    //Botones Segundo Piso Luces Encender
    public void BtnSegPisoIn(View view) {
        enviarMensaje("20");
    }
    //Botones Segundo Piso Luces Apagar
    public void BtnSegPisoOff(View view) {
        enviarMensaje("21");
    }
    //Botones Garage Luces Encender
    public void BtnGarageIn(View view) {
        enviarMensaje("30");
    }
    //Botones Garage Luces Apagar
    public void BtnGarageOff(View view) {
        enviarMensaje("31");
    }
    //Botones Piso Izquierdo Luces Encender
    public void BtnLeftIn(View view) {
        enviarMensaje("40");
    }
    //Botones Piso Izquierdo Luces Apagar
    public void BtnLeftOff(View view) {
        enviarMensaje("41");
    }
    //Botones Piso Derecho Luces Encender
    public void BtnRightIn(View view) {
        enviarMensaje("50");
    }
    //Botones Piso Derecho Luces Apagar
    public void BtnRightOff(View view) {
        enviarMensaje("51");
    }
    //Puerta Garage Abrir
    public void BtnGarageOpen(View view) {
        enviarMensaje("90");
    }
    //Puerta Garage Cerrar
    public void BtnGarageClose(View view) {
        enviarMensaje("91");
    }
    //Apagar Todas las Luces y Cerrar Garage
    public void BtnAllOff(View view) {
        enviarMensaje("1");
    }


    private boolean checkPermissions() {
        // Verificar si se han concedido todos los permisos requeridos
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (checkPermissions()) {
                // Los permisos fueron concedidos, continuar con la lógica de la aplicación
                // ...
            } else {
                // Los permisos fueron rechazados, mostrar un mensaje o tomar alguna acción adecuada
                Toast.makeText(this, "Los permisos de Bluetooth fueron rechazados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        conectarBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        desconectarBluetooth();
    }

    @SuppressLint("MissingPermission")
    private void conectarBluetooth() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(DEVICE_NAME)) {
                    bluetoothDevice = device;
                    break;
                }
            }
        }

        if (bluetoothDevice == null) {
            Toast.makeText(this, "No se encontró el dispositivo Bluetooth HC-06 emparejado", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Toast.makeText(this, "Conexión Bluetooth establecida", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al conectar con el dispositivo Bluetooth HC-06", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void desconectarBluetooth() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enviarMensaje(String mensaje) {
        if (outputStream == null) {
            Toast.makeText(this, "Error: OutputStream no inicializado", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            outputStream.write(mensaje.getBytes());
            outputStream.flush();
            Toast.makeText(this, "Mensaje enviado: " + mensaje, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}