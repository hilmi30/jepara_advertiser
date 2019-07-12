package com.perusdajepara.jeparaadvertiser.mainmenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.model.OverviewModel
import kotlinx.android.synthetic.main.activity_info_iklan.*
import kotlinx.android.synthetic.main.row_overview.view.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class InfoIklanActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var firebaseAdapter: FirebaseRecyclerAdapter<OverviewModel, ViewHolder>? = null
    var listener: ValueEventListener? = null
    var linkMoreInfo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_iklan)
        supportActionBar?.title = getString(R.string.edisi_bulan_ini)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().reference

        listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                toast(getString(R.string.terjadi_kesalahan))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                linkMoreInfo = p0?.value.toString()
            }

        }

        mDatabase?.child(Constant.LINK_MORE)?.addValueEventListener(listener)

        more_info.onClick {
            if(linkMoreInfo != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(linkMoreInfo)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val query = mDatabase?.child(Constant.EDISI_BULAN_INI)?.orderByChild(Constant.ITEM)
        val options = FirebaseRecyclerOptions.Builder<OverviewModel>()
                .setQuery(query!!, OverviewModel::class.java).build()

        firebaseAdapter = object : FirebaseRecyclerAdapter<OverviewModel, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.row_overview, parent, false)
                return ViewHolder(v)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: OverviewModel) {
                holder.namaOverview.text = model.item
            }

        }

        firebaseAdapter?.startListening()

        overview_recy.layoutManager = LinearLayoutManager(ctx)
        overview_recy.setHasFixedSize(true)
        overview_recy.adapter = firebaseAdapter

    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val namaOverview = v.overview_nama
    }

    override fun onStop() {
        super.onStop()
        firebaseAdapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        firebaseAdapter?.startListening()
    }

    override fun onRestart() {
        super.onRestart()
        firebaseAdapter?.startListening()
    }
}
