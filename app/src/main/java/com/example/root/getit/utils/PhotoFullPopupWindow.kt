package com.example.root.getit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.ProgressBar

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.widget.Button
import androidx.palette.graphics.Palette
import com.example.root.getit.R

class PhotoFullPopupWindow(ctx: Context, layout: Int, v: View, imageUrl: String?, bitmap: Bitmap?) : PopupWindow((ctx.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.popup_photo_full, null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) {

    internal lateinit var view: View
    internal lateinit var mContext: Context
    internal lateinit var photoView: PhotoView
    internal lateinit var loading: ProgressBar
    internal lateinit var parent: ViewGroup


    init {
        if (imageUrl != null) {


            if (Build.VERSION.SDK_INT >= 21) {
                elevation = 5.0f
            }
            this.mContext = ctx
            this.view = contentView
            val closeButton: androidx.appcompat.widget.AppCompatImageButton  = view.findViewById(R.id.ib_close)
            isOutsideTouchable = true

            isFocusable = true
            // Set a click listener for the popup window close button
            closeButton!!.setOnClickListener(View.OnClickListener {
                // Dismiss the popup window
                dismiss()
            })
            //---------Begin customising this popup--------------------

            photoView = view.findViewById(R.id.image)
            loading = view.findViewById(R.id.loading)
            photoView.maximumScale = 6f
            parent = photoView.parent as ViewGroup
            // ImageUtils.setZoomable(imageView);
            //----------------------------
            if (bitmap != null) {
                loading.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= 16) {
                    parent.background = BitmapDrawable(mContext.resources, Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))// ));
                } else {
                    onPalette(Palette.from(bitmap).generate())

                }
                photoView.setImageBitmap(bitmap)
            } else {
                loading.isIndeterminate = true
                loading.visibility = View.VISIBLE

                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)

                Glide.with(ctx)
                        .asBitmap()
                        .load(imageUrl)
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                                loading.isIndeterminate = false
                                loading.setBackgroundColor(Color.LTGRAY)
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    parent.background = BitmapDrawable(mContext.resources, Constants.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true)))// ));
                                } else {
                                    onPalette(Palette.from(resource).generate())

                                }
                                photoView.setImageBitmap(resource)

                                loading.visibility = View.GONE
                                return false
                            }
                        })
                        .apply(options)
                        .into(photoView)

                showAtLocation(v, Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0)
            }
            //------------------------------

        } else {

        }

    }

    fun onPalette(palette: Palette?) {
        if (null != palette) {
            val parent = photoView.parent.parent as ViewGroup
            parent.setBackgroundColor(palette!!.getDarkVibrantColor(Color.GRAY))
        }
    }

    companion object {
        private val instance: PhotoFullPopupWindow? = null
    }

}