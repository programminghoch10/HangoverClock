package com.JJ.hangoverclock.compat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ResourcesCompat {
    private static final Map<Integer, Typeface> cache = new HashMap<>();
    
    public static Typeface getFont(Context context, int id) {
        if (cache.containsKey(id))
            return cache.get(id);
        Resources resources = context.getResources();
        try {
            final File tempFile = File.createTempFile("font", "ttf");
            tempFile.deleteOnExit();
            final InputStream in = resources.openRawResource(id);
            final OutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            Typeface typeface = Typeface.createFromFile(tempFile);
            cache.put(id, typeface);
            return typeface;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
