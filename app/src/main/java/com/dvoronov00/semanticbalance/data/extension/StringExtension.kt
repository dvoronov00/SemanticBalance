package com.dvoronov00.semanticbalance.data.extension

import android.net.Uri

fun String.toTelUri() = Uri.parse("tel:$this")