package com.example.aptikma_remake.util

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

class LoadingDialog(private val context: Context) {

    private var progressDialog: SweetAlertDialog? = null

    fun showLoading() {
        if (progressDialog == null) {
            progressDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            progressDialog!!.titleText = "Loading"
            progressDialog!!.setCancelable(false)
        }
        progressDialog!!.show()
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismissWithAnimation()
        }
    }
}


