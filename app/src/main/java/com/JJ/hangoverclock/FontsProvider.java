package com.JJ.hangoverclock;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

class FontsProvider {
	private static final List<String> fonts = new LinkedList<String>() {{
		add("default");
	}};
	
	static List<String> getFonts() {
		return new LinkedList<>(fonts);
	}
	
	static void collectfonts(Context context) {
		Field[] fontfields = R.font.class.getFields();
		fonts.set(0, context.getString(R.string.defaultfonttext));
		for (int i = 1; i < fontfields.length + 1; i++) {
			Field fontfield = fontfields[i - 1];
			String fontname;
			try {
				fontname = fontfield.getName().replace("_", " ");
			} catch (Exception e) {
				fontname = null;
			}
			//Log.d(TAG, "collectfonts: fontname " + i + " is \"" + fontname + "\"");
			if (fontname != null) {
				boolean indexexists;
				try {
					//noinspection ResultOfMethodCallIgnored
					fonts.get(i);
					indexexists = true;
				} catch (IndexOutOfBoundsException indexerr) {
					indexexists = false;
				}
				if (!indexexists) {
					fonts.add(i, fontname);
				} else {
					fonts.set(i, fontname);
				}
			}
		}
	}
}
