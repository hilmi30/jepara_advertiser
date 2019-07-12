package com.perusdajepara.jeparaadvertiser.mainmenu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import kotlinx.android.synthetic.main.activity_tentang.*

class TentangActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)

        mDatabase = FirebaseDatabase.getInstance().reference
        val tentangRef = mDatabase?.child(Constant.TENTANG_JADS)
        tentangRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val deskripsi = p0?.child(Constant.DESKRIPSI)?.value.toString()
                kontent_tentang_jads?.text = deskripsi
            }

        })
    }
}
