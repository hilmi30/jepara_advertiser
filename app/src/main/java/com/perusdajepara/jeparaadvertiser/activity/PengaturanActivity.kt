package com.perusdajepara.jeparaadvertiser.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_pengaturan.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class PengaturanActivity : AppCompatActivity() {

    var pilihBool: Boolean? = null
    var perubahan: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)

        supportActionBar?.title = getString(R.string.pengaturan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pilihBool = Paper.book().read<Boolean>(Constant.PILIH_SETTING)

        sw_pilih_aksi.isChecked = pilihBool!!
        sw_pilih_aksi.setOnCheckedChangeListener { buttonView, isChecked ->
            perubahan = !perubahan
            pilihBool = isChecked
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.pengaturan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.simpan_pengaturan -> {
                if(perubahan) alertSimpan() else finish()
            }
            else -> {
                finish()
            }
        }

        return true
    }

    private fun alertSimpan() {
        alert {
            title = getString(R.string.simpan_pengaturan)
            message = getString(R.string.yakin_simpan_pengaturan)
            positiveButton(getString(R.string.ya)) {
                perubahan = !perubahan
                Paper.book().write(Constant.PILIH_SETTING, pilihBool)
                toast(getString(R.string.berhasil_disimpan))
            }
            negativeButton(getString(R.string.tidak)) {
                sw_pilih_aksi.isChecked = !pilihBool!!
            }
            isCancelable = false
        }.show()
    }

    override fun onBackPressed() {

        if(perubahan) {
            alertSimpan()
        } else {
            super.onBackPressed()
        }
    }
}
