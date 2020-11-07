package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.preferences.types.FlatType
import java.util.*

class CharArrayType: FlatType<CharArray>(
    { it.toCharArray() },
    { String(it) })

class ByteArrayType: FlatType<ByteArray>(
    { Base64.getDecoder().decode(it) },
    { Base64.getEncoder().encodeToString(it) })