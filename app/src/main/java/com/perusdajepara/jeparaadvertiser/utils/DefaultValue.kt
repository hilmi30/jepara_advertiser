package com.perusdajepara.jeparaadvertiser.utils

import io.paperdb.Paper

class DefaultValue {
    companion object {
        fun setDefaultValue() {
            val pilihBool = Paper.book().read<Boolean>(Constant.PILIH_SETTING)
            if(pilihBool == null) Paper.book().write(Constant.PILIH_SETTING, false)
        }
    }
}