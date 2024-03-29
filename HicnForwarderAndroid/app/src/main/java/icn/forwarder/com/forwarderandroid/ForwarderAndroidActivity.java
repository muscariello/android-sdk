/*
 * Copyright (c) 2019 Cisco and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package icn.forwarder.com.forwarderandroid;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import icn.forwarder.com.supportlibrary.Forwarder;
import icn.forwarder.com.utility.Constants;
import icn.forwarder.com.utility.ResourcesEnumerator;

public class ForwarderAndroidActivity extends AppCompatActivity {

    private Spinner sourceIpSpinner;
    private EditText sourcePortEditText;
    private EditText nextHopIpEditText;
    private EditText nextHopPortEditText;
    private EditText configurationEditText;
    private EditText prefixEditText;
    private EditText netmaskEditText;
    private EditText capacityEditText;
    private Switch forwarderSwitch;
    private Button sourceIpRefreshButton;
    private List<String> sourceIpArrayList = new ArrayList<String>();
    private List<String> sourceNetworkInterfaceArrayList = new ArrayList<>();
    private HashMap<String, String> addressesMap = new HashMap<String, String>();
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarder_android);
        checkEnabledPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkEnabledPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkEnabledPermission(Manifest.permission.FOREGROUND_SERVICE);
        init();
    }

    public HashMap<String, String> getLocalIpAddress() {
        HashMap<String, String> addressesMap = new HashMap<String, String>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String[] addressSplitted = inetAddress.getHostAddress().toString().split("%");
                        addressesMap.put(intf.getName(), addressSplitted[0]);
                    }
                }
            }
        } catch (SocketException ex) {
            String LOG_TAG = null;
            Log.e(LOG_TAG, ex.toString());
        }
        return addressesMap;
    }

    private void checkEnabledPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        1);
            }
        }
    }

    private void init() {
        sourceIpSpinner = (Spinner) findViewById(R.id.source_ip_spinner);
        sharedPreferences = getSharedPreferences(Constants.FORWARDER_PREFERENCES, MODE_PRIVATE);
        addressesMap = getLocalIpAddress();
        for (String networkInterface : addressesMap.keySet()) {
            sourceIpArrayList.add(networkInterface + ": " + addressesMap.get(networkInterface));
            sourceNetworkInterfaceArrayList.add(networkInterface);
        }
        if (addressesMap.size() > 0) {
            ArrayAdapter<String> sourceIpSpinnerArrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, sourceIpArrayList);
            sourceIpSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sourceIpSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
            sourceIpSpinner.setAdapter(sourceIpSpinnerArrayAdapter);
            if (sourceNetworkInterfaceArrayList.indexOf(sharedPreferences.getString(ResourcesEnumerator.SOURCE_NETWORK_INTERFACE.key(), Constants.DEFAULT_SOURCE_INTERFACE)) > -1) {
                sourceIpSpinner.setSelection(sourceNetworkInterfaceArrayList.indexOf(sharedPreferences.getString(ResourcesEnumerator.SOURCE_NETWORK_INTERFACE.key(), Constants.DEFAULT_SOURCE_INTERFACE)));
            } else {
                sourceIpSpinner.setSelection(0);
            }
        }
        sourcePortEditText = findViewById(R.id.source_port_editext);
        sourcePortEditText.setText(sharedPreferences.getString(ResourcesEnumerator.SOURCE_PORT.key(), Constants.DEFAULT_SOURCE_PORT));
        sourceIpRefreshButton = findViewById(R.id.source_ip_refresh_button);
        sourceIpRefreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addressesMap = getLocalIpAddress();
                sourceIpArrayList.clear();
                sourceNetworkInterfaceArrayList.clear();
                for (String networkInterface : addressesMap.keySet()) {
                    sourceIpArrayList.add(networkInterface + ": " + addressesMap.get(networkInterface));
                    sourceNetworkInterfaceArrayList.add(networkInterface);
                }
                if (addressesMap.size() > 0) {
                    ArrayAdapter<String> sourceIpComboArrayAdapter = new ArrayAdapter<String>(v.getContext(),
                            android.R.layout.simple_spinner_item, sourceIpArrayList);
                    sourceIpComboArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sourceIpComboArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                    sourceIpSpinner.setAdapter(sourceIpComboArrayAdapter);
                    if (sourceNetworkInterfaceArrayList.indexOf(sharedPreferences.getString(ResourcesEnumerator.SOURCE_NETWORK_INTERFACE.key(), Constants.DEFAULT_SOURCE_INTERFACE)) > -1) {
                        sourceIpSpinner.setSelection(sourceNetworkInterfaceArrayList.indexOf(sharedPreferences.getString(ResourcesEnumerator.SOURCE_NETWORK_INTERFACE.key(), Constants.DEFAULT_SOURCE_INTERFACE)));
                    } else {
                        sourceIpSpinner.setSelection(0);
                    }
                }
            }
        });
        nextHopIpEditText = findViewById(R.id.next_hop_ip_editext);
        nextHopIpEditText.setText(sharedPreferences.getString(ResourcesEnumerator.NEXT_HOP_IP.key(), Constants.DEFAULT_NEXT_HOP_IP));
        nextHopPortEditText = findViewById(R.id.next_hop_port_editext);
        nextHopPortEditText.setText(sharedPreferences.getString(ResourcesEnumerator.NEXT_HOP_PORT.key(), Constants.DEFAULT_NEXT_HOP_PORT));

        configurationEditText = findViewById(R.id.configuration_edittext);
        configurationEditText.setText(sharedPreferences.getString(ResourcesEnumerator.CONFIGURATION.key(), Constants.DEFAULT_CONFIGURATION));
        prefixEditText = findViewById(R.id.prefix_editext);
        prefixEditText.setText(sharedPreferences.getString(ResourcesEnumerator.PREFIX.key(), Constants.DEFAULT_PREFIX));
        netmaskEditText = findViewById(R.id.netmask_edittext);
        netmaskEditText.setText(sharedPreferences.getString(ResourcesEnumerator.NETMASK.key(), Constants.DEFAULT_NETMASK));
        capacityEditText = findViewById(R.id.capacity_edittext);
        capacityEditText.setText(sharedPreferences.getString(ResourcesEnumerator.CAPACITY.key(),Constants.DEFAULT_CAPACITY));
        forwarderSwitch = findViewById(R.id.forwarder_switch);

        forwarderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);
                if (isChecked) {
                    forwarderSwitch.setText(Constants.ENABLED);
                    SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences(Constants.FORWARDER_PREFERENCES, MODE_PRIVATE).edit();
                    sharedPreferencesEditor.putString(ResourcesEnumerator.SOURCE_NETWORK_INTERFACE.key(), sourceNetworkInterfaceArrayList.get(sourceIpSpinner.getSelectedItemPosition()));
                    sharedPreferencesEditor.putString(ResourcesEnumerator.SOURCE_IP.key(), addressesMap.get(sourceNetworkInterfaceArrayList.get(sourceIpSpinner.getSelectedItemPosition())));
                    sharedPreferencesEditor.putString(ResourcesEnumerator.SOURCE_PORT.key(), sourcePortEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.NEXT_HOP_IP.key(), nextHopIpEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.NEXT_HOP_PORT.key(), nextHopPortEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.CONFIGURATION.key(), configurationEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.PREFIX.key(), prefixEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.NETMASK.key(), netmaskEditText.getText().toString());
                    sharedPreferencesEditor.putString(ResourcesEnumerator.CAPACITY.key(), capacityEditText.getText().toString());
                    sharedPreferencesEditor.commit();
                    sourceIpSpinner.setEnabled(false);
                    sourceIpRefreshButton.setEnabled(false);
                    sourcePortEditText.setEnabled(false);
                    nextHopIpEditText.setEnabled(false);
                    nextHopPortEditText.setEnabled(false);
                    prefixEditText.setEnabled(false);
                    netmaskEditText.setEnabled(false);
                    capacityEditText.setEnabled(false);
                    configurationEditText.setEnabled(false);
                    startForwarder();

                } else {
                    forwarderSwitch.setText(Constants.DISABLED);
                    sourceIpSpinner.setEnabled(true);
                    sourceIpRefreshButton.setEnabled(true);
                    sourcePortEditText.setEnabled(true);
                    nextHopIpEditText.setEnabled(true);
                    nextHopPortEditText.setEnabled(true);
                    prefixEditText.setEnabled(true);
                    netmaskEditText.setEnabled(true);
                    capacityEditText.setEnabled(true);
                    configurationEditText.setEnabled(true);
                    stopForwarder();
                }
            }

        });

        if (Forwarder.getInstance().isRunning()) {
            forwarderSwitch.setText(Constants.ENABLED);
            forwarderSwitch.setChecked(true);
        } else {
            forwarderSwitch.setText(Constants.DISABLED);
        }
    }

    private void startForwarder() {
        Intent intent = new Intent(this, icn.forwarder.com.service.ForwarderAndroidService.class);
        startService(intent);
    }

    private void stopForwarder() {
        Intent intent = new Intent(this, icn.forwarder.com.service.ForwarderAndroidService.class);
        stopService(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
