package com.maku.shyfy

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maku.shyfy.databinding.ActivityMyShyFyBinding
import timber.log.Timber


class MyShyFyActivity : AppCompatActivity(), WifiP2pManager.ChannelListener, WifiP2pManager.PeerListListener{

    //recyclerview
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val config = WifiP2pConfig()

    private lateinit var binding: ActivityMyShyFyBinding

    private val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001
    private var isWifiP2pEnabled = false

    //conetxt
    val mContext: Context =
        ShyFyApplication.applicationContext()


    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    var mChannel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null

    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private val peers = mutableListOf<WifiP2pDevice>()

     val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            Timber.d("devices " + refreshedPeers.size)

            val name = ArrayList<String>(refreshedPeers.size)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
//            (listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
            viewManager = LinearLayoutManager(this)
            viewAdapter = MyAdapter(name, this)

            recyclerView = binding.peerListView.apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter

            }

        }

        if (peers.isEmpty()) {
            Timber.d("No devices found")
            return@PeerListListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_shy_fy)

        mChannel = manager?.initialize(this, mainLooper, null)
        mChannel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(manager!!, channel, this)
        }

        //discover peers that are available
        manager?.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                Timber.d("peers discovered success ...")
            }

            override fun onFailure(reasonCode: Int) {
                Timber.d("peers failure ...")
            }
        })



        //coonect to device
//        mChannel?.also { channel ->
//            manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
//
//                override fun onSuccess() {
//                    //success logic
//                }
//
//                override fun onFailure(reason: Int) {
//                    //failure logic
//                    Toast.makeText(mContext, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
//                }
//            }
//            )}

        Timber.d("peers " + peers)

        checkLocationPermission();

    }

    override fun onPeersAvailable(peerList: WifiP2pDeviceList?) {
        if (peerList != null) {
            Timber.i( "Found some peers!!! " + peerList.deviceList.size)
        };
    }
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isWifiP2pEnabled = isWifiP2pEnabled
    }

    /** register the BroadcastReceiver with the intent values to be matched  */
    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onChannelDisconnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //helper methods
     fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) { // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("location_permission")
                    .setMessage("location_permission")
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
                            )
                        })
                    .create()
                    .show()
            } else { // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

   override fun onRequestPermissionsResult(
       requestCode: Int,
       permissions: Array<out String>,
       grantResults: IntArray
   ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) { // permission was granted, yay! Do the
// location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) { //Request location updates:
                        finish();
                    }
                } else { // permission denied, boo! Disable the
// functionality that depends on this permission.
                }
                return
            }
        }
    }




}
