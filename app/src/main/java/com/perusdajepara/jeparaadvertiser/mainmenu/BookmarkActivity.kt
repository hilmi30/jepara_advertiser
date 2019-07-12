package com.perusdajepara.jeparaadvertiser.mainmenu

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.perusdajepara.jeparaadvertiser.utils.CheckPermission
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.activity.PilihActivity
import com.robertsimoes.shareable.Shareable
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.row_bookmark.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream

class BookmarkActivity : AppCompatActivity() {

    var raw: ArrayList<String>? = null
    var idIklan: ArrayList<String>? = null
    var iklanNama: ArrayList<String>? = null
    lateinit var adapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        supportActionBar?.title = getString(R.string.bookmark)

        raw = Paper.book().read<ArrayList<String>>(Constant.RAW)
        idIklan = Paper.book().read<ArrayList<String>>(Constant.ID)
        iklanNama = Paper.book().read<ArrayList<String>>(Constant.NAMA)

        bookmark_kosong?.visibility = View.GONE
        if(idIklan == null || idIklan?.isEmpty()!!){
            bookmark_kosong?.visibility = View.VISIBLE
        }

        if(raw == null || idIklan == null || iklanNama == null){
            raw = ArrayList()
            idIklan = ArrayList()
            iklanNama = ArrayList()

            // simpan data ke paper
            simpanKePaper(raw, idIklan, iklanNama)
        }

        bookmark_recy?.setHasFixedSize(true)
        bookmark_recy?.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()

        raw = Paper.book().read<ArrayList<String>>(Constant.RAW)
        idIklan = Paper.book().read<ArrayList<String>>(Constant.ID)
        iklanNama = Paper.book().read<ArrayList<String>>(Constant.NAMA)

        adapter = BookmarkAdapter(raw!!, idIklan!!, iklanNama!!){ id, raw ->
            startActivity<PilihActivity>(
                    Constant.ID to id,
                    Constant.RAW to raw
            )
        }

        bookmark_recy?.adapter = adapter
    }

    fun simpanKePaper(raw: ArrayList<String>?, idIklan: ArrayList<String>?, iklanNama: ArrayList<String>?){

        Paper.book().write(Constant.RAW, raw)
        Paper.book().write(Constant.ID, idIklan)
        Paper.book().write(Constant.NAMA, iklanNama)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id){
            R.id.delete_all -> {
                if(idIklan?.size!! <= 0){
                    toast(getString(R.string.bookmark_dihapus_semua))
                } else {
                    alert {
                        title = getString(R.string.hapus_bookmark)
                        message = getString(R.string.hapus_semua_bookmark)
                        positiveButton(getString(R.string.ya)) {
                            hapusSemuaBookmark()
                        }
                        negativeButton(getString(R.string.tidak)) {

                        }
                    }.show()
                }
            }
            else -> {
                finish()
            }
        }
        return true
    }

    private fun hapusSemuaBookmark() {
        // hapus semua data bookmark
        raw?.clear()
        idIklan?.clear()
        iklanNama?.clear()

        // simpan data ke paper
        simpanKePaper(raw, idIklan, iklanNama)

        adapter.notifyDataSetChanged()

        toast(getString(R.string.bookmark_dihapus_semua))
        bookmark_recy?.visibility = View.VISIBLE
    }

    class BookmarkAdapter(val raw: ArrayList<String>,
                          val idIklan: ArrayList<String>,
                          val iklanName: ArrayList<String>,
                          val listener: (String, String) -> Unit)
        : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_bookmark, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return iklanName.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val namaData = iklanName[position]
            val idData = idIklan[position]
            val rawData = raw[position]

            val context = holder.itemView.context

            holder.namaSitus.text = namaData

            holder.itemView.onClick {
                listener(idData, rawData)
            }

            holder.shareSitus.onClick {

                if(CheckPermission.isWriteStorageGranted(context)){

                    val multiFormatWriter = MultiFormatWriter()
                    try {
                        val bitMatrix = multiFormatWriter.encode(rawData, BarcodeFormat.QR_CODE, 300, 300)
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.createBitmap(bitMatrix)

                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", null)
                        val uriImg = Uri.parse(path)
                        val qrShare = Shareable.Builder(context)
                                .message("${namaData.toUpperCase()} - " + context.getString(R.string.msg_share))
                                .url(context.getString(R.string.link_app))
                                .image(uriImg)
                                .build()

                        qrShare.share()
                    } catch (e: WriterException){
                        e.printStackTrace()
                    }

                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Constant.WRITE_STORAGE_REQUEST_CODE)
                }
            }

            holder.hapusSitus.onClick {
                context.alert {
                    title = context.getString(R.string.hapus_bookmark)
                    message = context.getString(R.string.yakin_ingin_hapus_bookmark)
                    positiveButton(context.getString(R.string.ya)) {
                        hapusBookmark(idData, idIklan, raw, iklanName, holder.itemView.context)
                    }
                    negativeButton(context.getString(R.string.tidak)) {
                    }
                }.show()
            }
        }

        private fun hapusBookmark(idData: String, idIklan: ArrayList<String>, raw: ArrayList<String>,
                                  iklanName: ArrayList<String>, itemView: Context) {
            // hapus dari array
            val index = idIklan.indexOf(idData)
            idIklan.remove(idData)
            raw.removeAt(index)
            iklanName.removeAt(index)

            // Simpan array ke Paper
            BookmarkActivity().simpanKePaper(raw, idIklan, iklanName)
            notifyDataSetChanged()

            itemView.toast(itemView.getString(R.string.bookmark_dihapus))
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val namaSitus = v.nama_situs
            val hapusSitus = v.hapus_situs
            val shareSitus = v.share_situs
        }
    }
}
