package com.perusdajepara.jeparaadvertiser.mainmenu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.activity.PilihActivity
import com.perusdajepara.jeparaadvertiser.model.StatistikModel
import com.perusdajepara.jeparaadvertiser.utils.Constant
import kotlinx.android.synthetic.main.activity_statistik.*
import kotlinx.android.synthetic.main.row_statistik.view.*
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList

class StatistikActivity : AppCompatActivity() {

    var counterRef: DatabaseReference? = null
    var dayRef: DatabaseReference? = null
    var mDataRef: DatabaseReference? = null
    var firebaseAdapter: FirebaseRecyclerAdapter<StatistikModel, StatsViewHolder>? = null
    var monthItem: ArrayList<String>? = null
    var monthKey: ArrayList<String>? = null
    var dayItem: ArrayList<String>? = null
    var finalMonth: String? = null
    var query: Query? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistik)
        supportActionBar?.title = getString(R.string.statistik)

        mDataRef = FirebaseDatabase.getInstance().reference

        // Recycler Statistik
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        statsRecy?.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()

        // Kirim data counter ke firebase
        counterRef = mDataRef?.child(Constant.STATISTIK_ADS)
        counterRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e(Constant.ERROR, p0?.code.toString())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                monthItem = ArrayList()
                monthKey = ArrayList()
                for (month in p0?.children!!){
                    val value = month.key.toString()
                    val key = value.substring(0, 2)
                    val bulan = value.substring(2, value.length - 4)
                    val tahun = value.substring(value.length - 4)
                    val finalDate = "$bulan $tahun"
                    monthItem?.add(finalDate)
                    monthKey?.add(key)
                }
                val adapterWaktu = ArrayAdapter<String>(this@StatistikActivity,
                        R.layout.support_simple_spinner_dropdown_item, monthItem)
                adapterWaktu.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinnerBulan?.adapter = adapterWaktu
                val currentDate = monthItem?.lastIndex
                spinnerBulan?.setSelection(currentDate!!)
            }
        })

        spinnerBulan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = p0?.getItemAtPosition(p2).toString()
                val key = monthKey?.get(p2)
                val currentMonth = value.replace(" ", "")
                finalMonth = "$key$currentMonth"

                dayRef = counterRef?.child(finalMonth)
                dayRef?.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.e(Constant.ERROR, p0?.code.toString())
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        dayItem = ArrayList()
                        for (day in p0?.children!!){
                            val tanggal = day?.key.toString()
                            dayItem?.add(tanggal)
                        }

                        val adapterTgl = ArrayAdapter<String>(this@StatistikActivity,
                                R.layout.support_simple_spinner_dropdown_item, dayItem)
                        adapterTgl.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                        spinnerTanggal?.adapter = adapterTgl
                        val currentDay = dayItem?.lastIndex
                        spinnerTanggal?.setSelection(currentDay!!)
                    }

                })
            }
        }

        spinnerTanggal?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tanggal = parent?.getItemAtPosition(position).toString()
                query = counterRef?.child(finalMonth)?.child(tanggal)?.orderByChild(Constant.COUNT)
                showFirebaseList(query)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAdapter?.startListening()
    }

    private fun showFirebaseList(query: Query?) {
        val options = FirebaseRecyclerOptions.Builder<StatistikModel>()
                .setQuery(query!!, StatistikModel::class.java).build()

        firebaseAdapter = object : FirebaseRecyclerAdapter<StatistikModel, StatsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.row_statistik, parent, false)
                return StatsViewHolder(v)
            }

            override fun onBindViewHolder(holder: StatsViewHolder, position: Int, model: StatistikModel) {
                holder.setName(model.name!!)
                holder.setCount(model.count!!)
                holder.v.setOnClickListener {
                    val id = getRef(position).key
                    startActivity<PilihActivity>(
                            Constant.ID to id,
                            Constant.RAW to model.raw
                    )
                }
            }
        }

        firebaseAdapter?.startListening()
        statsRecy?.adapter = firebaseAdapter
    }

    class StatsViewHolder(var v: View): RecyclerView.ViewHolder(v){
        fun setName(name: String){
            val nama = v.url_name
            nama.text = name
        }

        fun setCount(count: Long){
            val hitung = v.count
            hitung.text = "$count"
        }
    }

    override fun onStop() {
        super.onStop()
        firebaseAdapter?.stopListening()
    }
}
