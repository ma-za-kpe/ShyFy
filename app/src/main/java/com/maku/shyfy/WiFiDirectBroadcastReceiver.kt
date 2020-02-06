package com.maku.shyfy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import com.maku.shyfy.databinding.ActivityMyShyFyBinding
import timber.log.Timber


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: MyShyFyActivity
) : BroadcastReceiver() {

    private lateinit var binding: ActivityMyShyFyBinding

    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                // UI update to indicate wifi p2p status.
                // UI update to indicate wifi p2p status.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        // Wifi P2P is enabled
                        Timber.d("Wifi P2P has been enabled")
                        activity.setIsWifiP2pEnabled(true);
                    }
                    else -> {
                        // Wi-Fi P2P is not enabled
                        Timber.d("Wifi P2P has not been enabled")
                        activity.setIsWifiP2pEnabled(false);
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                Timber.d( "P2P peers discovered")
                manager.requestPeers(channel, activity as PeerListListener?)

//                manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
//                    // Handle peers
//                    Timber.d("peers..." + peers)
//                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
               Timber.d("you have a new connection")

                if (manager == null) {
                    return
                }
                val networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo
                if (networkInfo.isConnected) { // we are connected with the other device, request connection
// info to find group owner IP
                }
                if (networkInfo.isConnected) { // we are connected with the other device, request connection
// info to find group owner IP
//                    manager.requestConnectionInfo(channel, connectionInfoListener)

                } else { // It's a disconnect
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
                Timber.d("wifi state changing ...")
                val thisDevice: WifiP2pDevice =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                Timber.d("......" + thisDevice.deviceAddress)

            }
        }
    }
}