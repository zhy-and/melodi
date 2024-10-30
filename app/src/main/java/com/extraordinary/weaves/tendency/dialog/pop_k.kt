package com.extraordinary.weaves.tendency.dialog

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.extraordinary.weaves.tendency.R


@JvmOverloads
fun createDialog(
    context: Context,
    layoutResId: Int,
    isCanceled: Boolean,
    gravity: Int = Gravity.BOTTOM,
    style: Int? = null,
    block: (AppCompatDialog.() -> Unit)? = null
): AppCompatDialog = AppCompatDialog(context, R.style.PopTheme).also { dialog ->
    dialog.setContentView(layoutResId)
    dialog.setCanceledOnTouchOutside(isCanceled)
    val window = dialog.window
    val layoutParams = window?.attributes
    layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
    layoutParams?.gravity = gravity
    window?.attributes = layoutParams
    style?.let { window?.setWindowAnimations(it) }
    block?.let { it(dialog) }
}

@JvmOverloads
fun showComDialog(
    context: Context,
    layoutResId: Int,
    isCanceled: Boolean = true,
    gravity: Int = Gravity.BOTTOM,
    style: Int? = null,
    block: (AppCompatDialog.() -> Unit)? = null
): AppCompatDialog = createDialog(context, layoutResId, isCanceled, gravity, style, block).apply {
    show()
}