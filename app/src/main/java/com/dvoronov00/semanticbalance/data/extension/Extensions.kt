package com.dvoronov00.semanticbalance.data.extension

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormattedDate(): String =
    SimpleDateFormat("dd.MM.yyyy hh:mm", Locale("ru", "RU"))
        .format(this)

fun String.toTelUri() = Uri.parse("tel:$this")