package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(src)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                    .circleCrop()
            )
            .into(view)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@BindingAdapter("externalLink")
fun externalLink(view: TextView, url: String?) {
    if (TextUtils.isEmpty(url)) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        view.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(v.context, intent, null)
        }
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}
