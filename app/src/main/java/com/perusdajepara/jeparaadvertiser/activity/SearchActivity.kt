package com.perusdajepara.jeparaadvertiser.activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.model.IklanModel
import com.perusdajepara.jeparaadvertiser.R
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.row_cari_list_iklan.view.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity


class SearchActivity : AppCompatActivity() {

    var check: Int? = null
    var filterType: Boolean? = null

    private lateinit var mDatabase: DatabaseReference
    var firebaseAdapter: FirebaseRecyclerAdapter<IklanModel, ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.title = getString(R.string.cari_iklan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().reference
        filterType = true
    }

    override fun onStart() {
        super.onStart()

        val allQuery = mDatabase.child(Constant.ADVERTISER)
        setFirebaseList(allQuery)

        cari_iklan_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = mDatabase.child(Constant.ADVERTISER).orderByChild(Constant.NAME)
                        .startAt(s.toString()).endAt(s.toString() + "\uf8ff")

                setFirebaseList(query)
            }

        })

    }

    private fun setFirebaseList(query: Query) {
        val options = FirebaseRecyclerOptions.Builder<IklanModel>()
                .setQuery(query, IklanModel::class.java).build()

        firebaseAdapter = object : FirebaseRecyclerAdapter<IklanModel, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cari_list_iklan, parent, false)
                return ViewHolder(v)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: IklanModel) {
                holder.name.text = model.name
                holder.itemView.onClick {
                    val id = getRef(position).key
                    startActivity<PilihActivity>(
                            Constant.ID to id,
                            Constant.RAW to model.raw
                    )
                }
            }

        }

        firebaseAdapter?.startListening()

        iklan_recy.layoutManager = LinearLayoutManager(ctx)
        iklan_recy.setHasFixedSize(true)
        iklan_recy.adapter = firebaseAdapter
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val name = v.nama_list_iklan
    }

    override fun onStop() {
        super.onStop()
        firebaseAdapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        firebaseAdapter?.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.cari_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.filter -> {
                filterDialog()
            }
            else -> {
                finish()
            }
        }
        return true
    }

    @SuppressLint("InflateParams")
    private fun filterDialog() {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_filter, null)
        val alertDialog = AlertDialog.Builder(ctx)

        if(filterType!!) view.ascending.isChecked = true else view.descending.isChecked = true

        view.filterGroup.setOnCheckedChangeListener { group, checkedId ->
            check = checkedId
        }

        alertDialog.setView(view)
                .setPositiveButton(getString(R.string.terapkan)) { dialog, _ ->
                   when(check) {
                       R.id.ascending -> {
                           filterAsc()
                       }
                       R.id.descending -> {
                           filterDesc()
                       }
                       else -> {
                           filterAsc()
                       }
                   }
                }
                .setNegativeButton(getString(R.string.batal)) { dialog, _ ->

                }
                .setCancelable(false)
                .create().show()
    }

    private fun filterAsc() {
        val layoutManager = LinearLayoutManager(ctx)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        iklan_recy.layoutManager = layoutManager
        filterType = true
    }

    private fun filterDesc() {
        val layoutManager = LinearLayoutManager(ctx)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        iklan_recy.layoutManager = layoutManager
        filterType = false
    }
}
