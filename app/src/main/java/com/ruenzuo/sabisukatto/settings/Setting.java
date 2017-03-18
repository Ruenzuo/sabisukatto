package com.ruenzuo.sabisukatto.settings;

import android.content.Context;

import com.ruenzuo.sabisukatto.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruenzuo on 18/03/2017.
 */

public class Setting {

    private String title;
    private String value;

    public Setting(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
