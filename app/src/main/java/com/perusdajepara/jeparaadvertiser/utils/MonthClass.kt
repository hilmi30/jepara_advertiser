package com.perusdajepara.jeparaadvertiser.utils

class MonthClass {

    companion object {
        fun changeMonth(month: Int): String?{

            var nameMonth: String? = null

            when(month){
                0 -> {
                    nameMonth = "a_Januari"
                }
                1 -> {
                    nameMonth = "b_Februari"
                }
                2 -> {
                    nameMonth = "c_Maret"
                }
                3 -> {
                    nameMonth = "d_April"
                }
                4 -> {
                    nameMonth = "e_Mei"
                }
                5 -> {
                    nameMonth = "f_Juni"
                }
                6 -> {
                    nameMonth = "g_Juli"
                }
                7 -> {
                    nameMonth = "h_Agustus"
                }
                8 -> {
                    nameMonth = "i_September"
                }
                9 -> {
                    nameMonth = "j_Oktober"
                }
                10 -> {
                    nameMonth = "k_November"
                }
                11 -> {
                    nameMonth = "l_Desember"
                }
            }

            return nameMonth
        }
    }
}