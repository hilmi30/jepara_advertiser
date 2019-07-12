package com.perusdajepara.jeparaadvertiser.utils

class Validasi {
    fun checkWebView(url: String?, idIklan: String?, nama: String?, raw: String?): Boolean {

        var valid = true

        if(url.isNullOrEmpty() || idIklan.isNullOrEmpty() || nama.isNullOrEmpty() || raw.isNullOrEmpty()) {
            valid = false
        }

        return valid
    }

}