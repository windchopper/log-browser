package com.github.windchopper.tools.log.browser.preferences;

import com.github.windchopper.common.preferences.types.FlatType;

import java.util.Base64;

public class ByteArrayType extends FlatType<byte[]> {

    public ByteArrayType() {
        super(source -> Base64.getDecoder().decode(source), source -> Base64.getEncoder().encodeToString(source));
    }

}
