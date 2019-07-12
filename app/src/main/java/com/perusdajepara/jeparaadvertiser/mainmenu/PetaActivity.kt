package com.perusdajepara.jeparaadvertiser.mainmenu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import org.jetbrains.anko.alert

class PetaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mDataRef: DatabaseReference? = null
    var distRef: DatabaseReference? = null
    var nameChild: String? = null
    var nameTitle: String? = null
    var distListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peta)

        nameChild = intent.getStringExtra(Constant.NAME_CHILD)
        nameTitle = intent.getStringExtra(Constant.NAME_TITLE)

        supportActionBar?.title = nameTitle

        mDataRef = FirebaseDatabase.getInstance().reference
        distRef = mDataRef?.child(nameChild)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_distribusi) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!

        val jepara = LatLng(Constant.LAT_JEPARA, Constant.LNG_JEPARA)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jepara, Constant.ZOOM_MAP))

        val uiSetting = mMap.uiSettings
        uiSetting.isZoomControlsEnabled = true
        uiSetting.isMyLocationButtonEnabled = true
        uiSetting.isCompassEnabled = true

        distListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e(Constant.ERROR, p0?.code.toString())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                for(data in p0?.children!!){
                    val lat = data.child(Constant.LAT).value.toString()
                    val lng = data.child(Constant.LNG).value.toString()
                    val namaLokasi = data.child(Constant.NAME).value.toString()

                    val coord = LatLng(lat.toDouble(), lng.toDouble())
                    mMap.addMarker(MarkerOptions().position(coord).title(namaLokasi))
                }
            }
        }

        distRef?.addValueEventListener(distListener)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constant.FINE_LOCATION_CODE)
        } else {
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        distRef?.removeEventListener(distListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.peta_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        when(id){
            R.id.info -> {
                when(nameChild){
                    Constant.DISTRIBUSI -> {
                        showAlert(getString(R.string.peta_distribusi), getString(R.string.desc_peta_distribusi))
                    }
                    Constant.ADVERTISER -> {
                        showAlert(getString(R.string.peta_advertiser), getString(R.string.desc_peta_advertiser))
                    }
                    Constant.WISATA -> {
                        showAlert(getString(R.string.peta_wisata_jepara), getString(R.string.desc_peta_wisata_jepara))
                    }
                }
            }
            else -> {
                finish()
            }
        }

        return true
    }

    private fun showAlert(judul: String, msg: String){
        alert {
            title = judul
            message = msg
            negativeButton(getString(R.string.tutup)){
                it.dismiss()
            }
        }.show()
    }
}
