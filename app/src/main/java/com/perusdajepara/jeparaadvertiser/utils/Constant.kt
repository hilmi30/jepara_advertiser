package com.perusdajepara.jeparaadvertiser.utils

import com.perusdajepara.jeparaadvertiser.BuildConfig

class Constant {

    companion object {
        val version = BuildConfig.VERSION_NAME
        val APP_NAME = "Jepara Advertiser $version"

        val KEY = "PerusdaJeparaAdvertiser"
        val SALT = "InformationInARightWay"

        val RAW = "raw"
        val URL = "url"
        val ID = "idIklan"
        val NAMA = "nama"

        val NAME_CHILD = "name_child"
        val NAME_TITLE = "name_title"
        val DISTRIBUSI = "lokasi_distribusi"
        val ADVERTISER = "data_iklan"
        val WISATA = "lokasi_wisata_jepara"
        val STATISTIK_ADS = "statistik_ads"
        val NOMOR = "nomor"
        val KATEGORI = "kategori"
        val FIRST_LAUNCH = "first"
        val NOTE = "note"
        val ERROR = "error"

        val CAMERA_REQUEST_CODE = 100
        val WRITE_STORAGE_REQUEST_CODE = 200
        val FINE_LOCATION_CODE = 470
        val PERMISSION_CALLBACK_CONSTANT = 100
        val REQUEST_PERMISSION_SETTING = 102
        val REQUEST_CODE_QR_SCAN = 101
        val WRITE_CODE = 11

        val DESKRIPSI = "deskripsi"
        val TABEL = "tabel"
        val INFO_IKLAN_FIREBASE = "info_iklan"

        val LAT_JEPARA = -6.5804981
        val LNG_JEPARA = 110.6789833
        val ZOOM_MAP = 8.7F

        val NAME = "name"
        val LAT = "lat"
        val LNG = "lng"

        val SCAN_LIBRARY_NAME = "com.blikoon.qrcodescanner.got_qr_scan_relult"

        val COUNT = "count"
        val TENTANG_JADS = "tentang_jads"
        val DELAY_STATS = "delay_stats"

        val WHATSAPP_PACKAGE = "com.whatsapp"
        val GMAPS_PACKAGE = "com.google.android.apps.maps"

        val PILIH_SETTING = "pilih_setting"
        val EDISI_BULAN_INI = "edisi_bulan_ini"
        val ITEM = "item"
        val LINK_MORE = "link_more_info"

        fun whatsAppApi(nomor: String?): String {
            return "http://api.whatsapp.com/send?phone=$nomor&text="
        }

        fun geo(lat: String?, lng: String?, nama: String?): String {
            return "geo:0,0?q=$lat,$lng($nama)"
        }
    }
}