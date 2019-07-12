package com.perusdajepara.jeparaadvertiser.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.perusdajepara.jeparaadvertiser.utils.Constant
import com.perusdajepara.jeparaadvertiser.R
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.row_slide.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity

class WelcomeActivity : AppCompatActivity() {

    var pos: Int? = null
    var itemLength: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        pos = 0
        back_button?.visibility = View.GONE

        itemLength = SlideAdapter(this).slidetitle.size

        next_button?.setOnClickListener {
            if(pos!! < itemLength!! - 1){
                slide_layout?.currentItem = pos!! + 1
            } else {
                startActivity<MainActivity>()
                finish()
                Paper.book().write(Constant.FIRST_LAUNCH, true)
            }
        }

        back_button?.setOnClickListener {
            slide_layout?.currentItem = pos!! - 1
        }

        val adapter = SlideAdapter(this)
        slide_layout?.adapter = adapter
        dots_layout?.attachToViewPager(slide_layout)

        slide_layout?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                pos = position
                when (position) {
                    0 -> {
                        next_button?.visibility = View.VISIBLE
                        back_button?.visibility = View.GONE
                    }
                    itemLength!! - 1 -> next_button?.text = getString(R.string.selesai)
                    else -> {
                        next_button?.text = getString(R.string.next)
                        next_button?.visibility = View.VISIBLE
                        back_button?.visibility = View.VISIBLE
                    }
                }
            }

        })
    }

    class SlideAdapter(val c: Context): PagerAdapter() {

        val slideicon = listOf(
                R.drawable.jads_logo_new,
                R.drawable.qrcode,
                R.drawable.peta_advertiser,
                R.drawable.bookmark,
                R.drawable.info_iklan,
                R.drawable.statistik
        )
        val slidetitle = listOf(
                "Selamat Datang di Jepara Advertiser",
                "QR Code Scanner",
                "Peta Lokasi Distribusi, Advertiser dan Wisata Jepara",
                "Bookmark",
                "Info Iklan",
                "Statistik"
        )
        val slidedesc = listOf(
                "",
                "Jepara Advertiser hadir dengan fitur QR Code Scanner untuk melihat iklan lebih lengkap",
                "Anda dapat mencari titik lokasi distribusi Jepara Advertiser, lokasi pengiklan dan tempat wisata di Jepara dengan mudah",
                "Tandai iklan favorit anda untuk bisa dibuka kapan saja",
                "Dapatkan info tentang pemasangan iklan di Jepara Advertiser",
                "Lihat iklan yang sering dikunjungi untuk meningkatkan referensi anda"
        )

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as LinearLayout
        }

        override fun getCount(): Int {
            return slidetitle.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = c.layoutInflater.inflate(R.layout.row_slide, container, false)

            val iconslide = v.img_slide
            val titleslide = v.title_slide
            val descslide = v.desc_slide

            iconslide.setImageResource(slideicon[position])
            titleslide.text = slidetitle[position]
            descslide.text = slidedesc[position]

            container.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as LinearLayout)
        }
    }
}
