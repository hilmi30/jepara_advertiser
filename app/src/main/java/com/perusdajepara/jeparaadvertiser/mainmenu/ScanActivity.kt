package com.perusdajepara.jeparaadvertiser.mainmenu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.blikoon.qrcodescanner.QrCodeActivity
import com.perusdajepara.jeparaadvertiser.utils.CheckPermission
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.activity.PilihActivity
import com.perusdajepara.jeparaadvertiser.R
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.simbio.encryption.Encryption

class ScanActivity : AppCompatActivity() {

    private var iv: ByteArray? = null
    private var encryption: Encryption? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        iv = ByteArray(16)
        encryption = Encryption.getDefault(Constant.KEY, Constant.SALT, iv)

        openScan()
    }

    private fun openScan() {
        if(CheckPermission.isCameraGranted(this) && CheckPermission.isReadStorageGranted(this)){
            scanQRCode()
        } else {
            ActivityCompat.requestPermissions(this@ScanActivity, arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            ), Constant.PERMISSION_CALLBACK_CONSTANT)
        }
    }

    private fun scanQRCode() {
        val intent = Intent(this@ScanActivity, QrCodeActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_CODE_QR_SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constant.REQUEST_CODE_QR_SCAN && resultCode == Activity.RESULT_OK && data != null){
            val qrCodeValue = data.getStringExtra(Constant.SCAN_LIBRARY_NAME)
            if(qrCodeValue.length < 48){
                Log.e(Constant.ERROR, getString(R.string.string_tidak_sesuai))
            } else {
                val decrypText = qrCodeValue?.substring(0, qrCodeValue.length - 48)
                val finalText = encryption?.decryptOrNull(decrypText!!)

                if(decrypText.isNullOrEmpty() || finalText.isNullOrEmpty()){
                    Log.e(Constant.ERROR, getString(R.string.string_tidak_sesuai))
                } else {
                    startActivity<PilihActivity>(
                            Constant.ID to finalText,
                            Constant.RAW to qrCodeValue
                    )
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == Constant.PERMISSION_CALLBACK_CONSTANT){
            var allGranted = false
            for (grant in grantResults){
                allGranted = grant == PackageManager.PERMISSION_GRANTED
            }

            if(allGranted){
                scanQRCode()
            } else {
                toast(getString(R.string.izin_tidak_diberikan))
            }
        }
    }
}
