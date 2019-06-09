package com.github.windchopper.tools.log.browser.preferences;

import com.github.windchopper.common.preferences.types.FlatType;

public class CharArrayType extends FlatType<char[]> {

    public CharArrayType() {
        super(String::toCharArray, String::new);
    }

}
