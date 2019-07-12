package com.perusdajepara.jeparaadvertiser.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.utils.*
import com.robertsimoes.shareable.Shareable
import im.delight.android.webview.AdvancedWebView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_web_view.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class WebViewActivity : AppCompatActivity(), AdvancedWebView.Listener {

    var rawBookmark: ArrayList<String>? = null
    var bookmarkId: ArrayList<String>? = null
    var iklanName: ArrayList<String>? = null

    var menuWeb: Menu? = null

    var counterRef: DatabaseReference? = null
    var count: DatabaseReference? = null
    var countListener: ValueEventListener? = null

    var delayStatus: Boolean? = null

    // Item Penting!
    var url: String? = null
    var idIklan: String? = null
    var raw: String? = null
    var nama: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // menerima item dari pilih activity
        url = intent.getStringExtra(Constant.URL)
        idIklan = intent.getStringExtra(Constant.ID)
        raw = intent.getStringExtra(Constant.RAW)
        nama = intent.getStringExtra(Constant.NAMA)

        // status delay
        delayStatus = Paper.book().read<Boolean>(Constant.DELAY_STATS)

        // load webview
        webview.setListener(this, this)
        webview.loadUrl(url)

        // baca item bookmark
        rawBookmark = Paper.book().read<ArrayList<String>>(Constant.RAW)
        bookmarkId = Paper.book().read<ArrayList<String>>(Constant.ID)
        iklanName = Paper.book().read<ArrayList<String>>(Constant.NAMA)

        // Buat array baru jika ada salah satu item yang kosong
        if(rawBookmark == null || bookmarkId == null || iklanName == null){
            rawBookmark = ArrayList()
            bookmarkId = ArrayList()
            iklanName = ArrayList()
        }

        // swipe refresh
        swipe_refresh?.setOnRefreshListener { refreshWeb() }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()

        val c = Calendar.getInstance()

        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dateMonth = MonthClass.changeMonth(month) +year

        if(delayStatus == null || delayStatus == false){
            // kirim data statistik
            counterRef = FirebaseDatabase.getInstance().reference.child(Constant.STATISTIK_ADS).child(dateMonth).child("$day")

            val countRef = counterRef?.child(idIklan)
            count = countRef?.child(Constant.COUNT)
            val urlNameCount = countRef?.child(Constant.NAME)
            val urlCount = countRef?.child(Constant.URL)
            val urlRaw = countRef?.child(Constant.RAW)

            countListener = object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {
                    Log.e(Constant.ERROR, p0?.code.toString())
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    val value = p0?.value
                    urlNameCount?.setValue(nama)
                    urlRaw?.setValue(raw)
                    urlCount?.setValue(url)
                    if(value == null){
                        count?.setValue(1)
                    } else {
                        var valuePlus = value.toString().toInt()
                        valuePlus++
                        count?.setValue(valuePlus)
                    }
                }
            }
            count?.addListenerForSingleValueEvent(countListener)
            Paper.book().write(Constant.DELAY_STATS, true)
            startService(Intent(this, DelayServices::class.java))
        }
    }

    override fun onPageFinished(url: String?) {
        swipe_refresh?.isRefreshing = false
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        kesalahan.visibility = View.VISIBLE
    }

    override fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {
    }

    override fun onExternalPageRequest(url: String?) {
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        swipe_refresh?.isRefreshing = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webview.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        webview.onResume()
    }

    override fun onPause() {
        super.onPause()
        webview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webview.onDestroy()
    }

    private fun refreshWeb() {
        reloadUrl()
        Handler().postDelayed({
            swipe_refresh?.isRefreshing = false
        }, 2000)
    }

    private fun reloadUrl() {
        webview.reload()
        kesalahan.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuWeb = menu
        menuInflater.inflate(R.menu.webmenu, menu)

        if(bookmarkId?.contains(idIklan)!!){
            marked()
        } else {
            unmarked()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when(id){
            R.id.bookmark_menu -> {
                if(!Validasi().checkWebView(url, idIklan, nama, raw)) {
                    ctx.toast(getString(R.string.terjadi_kesalahan))
                } else {
                    // jika bookmark tidak ada di array
                    if(bookmarkId?.contains(idIklan)!!){

                        val index = bookmarkId?.indexOf(idIklan)

                        // hapus data dari array
                        bookmarkId?.remove(idIklan)
                        rawBookmark?.removeAt(index!!)
                        iklanName?.removeAt(index!!)

                        // simpan array ke paper
                        simpanKePaper(rawBookmark, bookmarkId, iklanName)

                        toast(getString(R.string.bookmark_dihapus))
                        // ganti icon
                        unmarked()
                    } else {
                        // jika bookmark sudah 20 item
                        if(bookmarkId?.size == 20){
                            toast(getString(R.string.batas_maksimal_bookmark))
                        } else {
                            // simpan di array
                            rawBookmark?.add(raw!!)
                            bookmarkId?.add(idIklan!!)
                            iklanName?.add(nama!!)

                            // simpan array di paper
                            simpanKePaper(rawBookmark, bookmarkId, iklanName)

                            toast(getString(R.string.bookmark_ditambahkan))
                            // ganti icon
                            marked()
                        }
                    }
                }
            }
            R.id.bookmark_share -> {
                if(!Validasi().checkWebView(url, idIklan, nama, raw)) {
                    ctx.toast(getString(R.string.terjadi_kesalahan))
                } else {
                    if(CheckPermission.isWriteStorageGranted(this)){

                        val multiFormatWriter = MultiFormatWriter()
                        try {
                            val bitMatrix = multiFormatWriter.encode(raw, BarcodeFormat.QR_CODE, 300, 300)
                            val barcodeEncoder = BarcodeEncoder()
                            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

                            val bytes = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "", null)
                            val uriImg = Uri.parse(path)
                            val qrShare = Shareable.Builder(this)
                                    .message("${nama?.toUpperCase()} - " + getString(R.string.msg_share))
                                    .url(getString(R.string.link_app))
                                    .image(uriImg)
                                    .build()

                            qrShare.share()
                        } catch (e: WriterException){
                            e.printStackTrace()
                        }

                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                Constant.WRITE_CODE)
                    }
                }
            }
            else -> {
                finish()
            }
        }
        return true
    }

    private fun marked(){
        menuWeb?.getItem(1)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white_24dp)
    }

    private fun unmarked(){
        menuWeb?.getItem(1)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_white_24dp)
    }

    fun simpanKePaper(rawPaper: ArrayList<String>?, bookmarkId: ArrayList<String>?,
                      iklanName: ArrayList<String>?){

        Paper.book().write(Constant.RAW, rawPaper)
        Paper.book().write(Constant.ID, bookmarkId)
        Paper.book().write(Constant.NAMA, iklanName)
    }
}
