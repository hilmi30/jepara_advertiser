package com.perusdajepara.jeparaadvertiser.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.mainmenu.ScanActivity
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_pilih.*
import kotlinx.android.synthetic.main.row_pilih.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class PilihActivity : AppCompatActivity() {

    var url: String? = null
    var nama: String? = null
    var raw: String? = null
    var id: String? = null
    var kategori: String? = null

    var nomor: String? = null
    var lat: String? = null
    var lng: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih)

        pilihMemuat.visibility = View.VISIBLE

        id = intent.getStringExtra(Constant.ID)
        raw = intent.getStringExtra(Constant.RAW)

        pilihRecy?.setHasFixedSize(true)
        pilihRecy?.layoutManager = GridLayoutManager(this, 2)

        val dataIklanRef = FirebaseDatabase.getInstance().reference.child(Constant.ADVERTISER).child(id)
        dataIklanRef?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

                pilihMemuat.visibility = View.INVISIBLE

                Log.d(Constant.ERROR, p0?.code.toString())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(!p0?.exists()!!){
                    pilihText.text = getString(R.string.tidak_ditemukan)
                    pilihMemuat.visibility = View.INVISIBLE
                } else {
                    url = p0.child(Constant.URL)?.value.toString()
                    nama = p0.child(Constant.NAME)?.value.toString()
                    nomor = p0.child(Constant.NOMOR)?.value.toString()
                    lat = p0.child(Constant.LAT)?.value.toString()
                    lng = p0.child(Constant.LNG)?.value.toString()
                    kategori = p0.child(Constant.KATEGORI)?.value.toString()


                    if(Paper.book().read(Constant.PILIH_SETTING)) {
                        startActivity<WebViewActivity>(
                                Constant.ID to id,
                                Constant.RAW to raw,
                                Constant.URL to url,
                                Constant.NAMA to nama
                        )
                        finish()
                    } else {

                        pilihMemuat.visibility = View.INVISIBLE

                        val dataRecy = ArrayList<PilihData>()
                        pilihText.text = nama

                        when(kategori){
                            "artikel" -> {
                                dataRecy.add(PilihData(R.drawable.icon_info, getString(R.string.info_lengkap)))
                                dataRecy.add(PilihData(R.drawable.qrcode, getString(R.string.qrcode)))
                            }
                            "iklan" -> {
                                dataRecy.add(PilihData(R.drawable.icon_info, getString(R.string.info_lengkap)))
                                dataRecy.add(PilihData(R.drawable.icon_whatshapp, getString(R.string.whatsapp)))
                                dataRecy.add(PilihData(R.drawable.icon_telepon, getString(R.string.telepon)))
                                dataRecy.add(PilihData(R.drawable.icon_sms, getString(R.string.sms)))
                                dataRecy.add(PilihData(R.drawable.icon_lokasi, getString(R.string.lokasi)))
                                dataRecy.add(PilihData(R.drawable.qrcode, getString(R.string.qrcode)))
                            }
                        }

                        val adapter = PilihAdapter(dataRecy) {
                            when (it.nama) {
                                getString(R.string.info_lengkap) -> {
                                    startActivity<WebViewActivity>(
                                            Constant.ID to id,
                                            Constant.RAW to raw,
                                            Constant.URL to url,
                                            Constant.NAMA to nama
                                    )
                                }
                                getString(R.string.whatsapp) -> {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(Constant.whatsAppApi(nomor))
                                        intent.`package` = Constant.WHATSAPP_PACKAGE
                                        startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        toast(getString(R.string.anda_belum_install_whatsapp))
                                    }
                                }
                                getString(R.string.telepon) -> {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL)
                                        intent.data = Uri.parse("tel:$nomor")
                                        startActivity(intent)
                                    } catch (e: Exception) {
                                        toast(getString(R.string.terjadi_kesalahan))
                                    }
                                }
                                getString(R.string.sms) -> {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse("sms:$nomor")
                                    startActivity(intent)
                                }
                                getString(R.string.lokasi) -> {
                                    try {
                                        val gmmIntentUri = Uri.parse(Constant.geo(lat, lng, nama))
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        mapIntent.`package` = Constant.GMAPS_PACKAGE
                                        startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        toast(getString(R.string.anda_belum_install_gmaps))
                                    }
                                }
                                getString(R.string.qrcode) -> {
                                    startActivity<ScanActivity>()
                                    finish()
                                }
                            }
                        }
                        pilihRecy?.adapter = adapter
                    }
                }
            }
        })

    }

    class PilihAdapter(var datas: ArrayList<PilihData>, var listener: (PilihData) -> Unit)
        : RecyclerView.Adapter<PilihAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pilih, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return datas.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(datas[position], listener)
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val image = v.imgNamaTxt
            val nama = v.pilihNamaTxt

            fun bindItem(pilih: PilihData, listener: (PilihData) -> Unit){

                Glide.with(itemView).load(pilih.img).into(image)
                nama.text = pilih.nama

                itemView.onClick {
                    listener(pilih)
                }
            }
        }
    }

    data class PilihData(
            var img: Int,
            var nama: String
    )
}
