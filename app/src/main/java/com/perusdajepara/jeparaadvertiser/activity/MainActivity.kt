package com.perusdajepara.jeparaadvertiser.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.bumptech.glide.Glide
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.utils.DefaultValue
import com.perusdajepara.jeparaadvertiser.utils.DelayServices
import com.perusdajepara.jeparaadvertiser.R
import com.perusdajepara.jeparaadvertiser.mainmenu.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_main_menu.view.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(ctx)
        DefaultValue.setDefaultValue()

        supportActionBar?.title = Constant.APP_NAME

        // Panggil delay service untuk statistik
        startService(Intent(this, DelayServices::class.java))

        val firstLaunch = Paper.book().read<Boolean>(Constant.FIRST_LAUNCH)
        if(firstLaunch == null || firstLaunch == false){
            startActivity<WelcomeActivity>()
            finish()
        }

        main_recy?.setHasFixedSize(true)
        main_recy?.layoutManager = GridLayoutManager(this, 2)

        val menuData = ArrayList<MenuData>()
        menuData.add(MenuData(R.drawable.qrcode, getString(R.string.qrcode)))
        menuData.add(MenuData(R.drawable.icon_edisi, getString(R.string.edisi_bulan_ini)))
        menuData.add(MenuData(R.drawable.peta_distribusi, getString(R.string.peta_distribusi)))
        menuData.add(MenuData(R.drawable.peta_advertiser, getString(R.string.peta_advertiser)))
        menuData.add(MenuData(R.drawable.peta_wisata, getString(R.string.peta_wisata_jepara)))
        menuData.add(MenuData(R.drawable.bookmark, getString(R.string.bookmark)))
        menuData.add(MenuData(R.drawable.statistik, getString(R.string.statistik)))
        menuData.add(MenuData(R.drawable.tentang_jads, getString(R.string.tentang)))

        val adapter = MainMenuAdapter(menuData) {
            when (it.nama) {
                getString(R.string.qrcode) -> {
                    startActivity<ScanActivity>()
                }
                getString(R.string.peta_distribusi) -> {
                    startActivity<PetaActivity>(
                            Constant.NAME_CHILD to Constant.DISTRIBUSI,
                            Constant.NAME_TITLE to getString(R.string.peta_distribusi)
                    )
                }
                getString(R.string.peta_advertiser) -> {
                    startActivity<PetaActivity>(
                            Constant.NAME_CHILD to Constant.ADVERTISER,
                            Constant.NAME_TITLE to getString(R.string.peta_advertiser)
                    )
                }
                getString(R.string.peta_wisata_jepara) -> {
                    startActivity<PetaActivity>(
                            Constant.NAME_CHILD to Constant.WISATA,
                            Constant.NAME_TITLE to getString(R.string.peta_wisata_jepara)
                    )
                }
                getString(R.string.bookmark) -> {
                    startActivity<BookmarkActivity>()
                }
                getString(R.string.edisi_bulan_ini) -> {
                    startActivity<InfoIklanActivity>()
                }
                getString(R.string.statistik) -> {
                    startActivity<StatistikActivity>()
                }
                getString(R.string.tentang) -> {
                    startActivity<TentangActivity>()
                }
            }
        }

        main_recy?.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.search -> {
                startActivity<SearchActivity>()
            }
            R.id.setting -> {
                startActivity<PengaturanActivity>()
            }
        }

        return true
    }

    class MainMenuAdapter(val menuData: ArrayList<MenuData>, val listener: (MenuData) -> Unit): RecyclerView.Adapter<MainMenuAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_main_menu, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return menuData.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(menuData[position], listener)
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v)  {
            val titleMenu = v.main_menu_title
            val menuIcon = v.main_menu_img

            fun bindItem(menu: MenuData, listener: (MenuData) -> Unit){
                titleMenu.text = menu.nama
                Glide.with(itemView).load(menu.icon).into(menuIcon)
                itemView.onClick {
                    listener(menu)
                }
            }
        }

    }

    data class MenuData(
            val icon: Int,
            val nama: String
    )
}
