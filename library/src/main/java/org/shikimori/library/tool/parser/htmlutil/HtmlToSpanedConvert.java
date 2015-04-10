package org.shikimori.library.tool.parser.htmlutil;

import android.text.Html;
import android.text.SpannableStringBuilder;

public class HtmlToSpanedConvert {
	private static final char[] B = {'<', 'b'};
	private static final char[] B_ = {'<', '/', 'b'};
	private static final char[] I = {'<', 'i'};
	private static final char[] I_ = {'<', '/', 'i'};
	private static final char[] U = {'<', 'u'};
	private static final char[] U_ = {'<', '/', 'u'};
	private static final char[] FONT = {'<', 'f'};
	private static final char[] FONT_ = {'<', '/', 'f'};
	private static final char[] BR = {'<', 'b', 'r'};
	private static final char END = '>';

	public CharSequence convert(String content) {
		if (content == null || content.indexOf('<') == -1)
			return content;
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		char[] chars = content.toLowerCase().toCharArray();
		int total = chars.length;
		int i = 0;
		while(i < total) {
			int start = content.indexOf('<', i);
			int end = start;
			if (start == -1) {
				ssb.append(content.substring(i));
				break;
			}
			if (isTag(chars, start, B)) {
				end = getIndex(chars, start, B_);
			} else if (isTag(chars, start, I)) {
				end = getIndex(chars, start, I_);
			} else if (isTag(chars, start, U)) {
				end = getIndex(chars, start, U_);
			} else if (isTag(chars, start, FONT)) {
				end = getIndex(chars, start, FONT_);
			} else if (isTag(chars, start, BR)) {
				end = getIndex(chars, start, END);
			} else {
				ssb.append(content.subSequence(i, start + 1));
				i = start + 1;
				continue;
			}
			ssb.append(content.subSequence(i, start));
			int tagEnd = Math.max(end, getIndex(chars, end, END));
			
			
		}
		return ssb;
	}
	
	private void convert(SpannableStringBuilder ssb, String str, int startIndex, int endIndex) {
		str = str.substring(startIndex, endIndex);
		String content = str.substring(str.indexOf('>') + 1, str.lastIndexOf('<'));
		if (!hasTag(content)) {
			ssb.append(Html.fromHtml(str));
		} else {
			
		}
	}
	
	private boolean hasTag(String str) {
		int index = str.indexOf('<');
		if (index == -1)
			return false;
		char[] chars = str.toCharArray();
		while (index < str.length()) {
			if (isTag(chars, index, B) || isTag(chars, index, I) || isTag(chars, index, U) || isTag(chars, index, FONT))
				return true;
		}
		return false;
	}
	
	private boolean hasEnd(char[] chars, int startIndex) {
		for (int i = startIndex; i < chars.length; i++) {
			if (chars[i] == END)
				return true;
		}
		return false;
	}
	
	private boolean isTag(char[] chars, int startIndex, char[] tag) {
		if (chars.length <= startIndex + 1 + tag.length)
			return false;
		for (int i = 0; i<tag.length; i++) {
			if (chars[i + startIndex] != tag[i])
				return false;
		}
		char next = chars[startIndex + tag.length];
		return (next > 'z' || next < 'a') && hasEnd(chars, startIndex + tag.length);
	}
	
	private int getIndex(char[] chars, int startIndex, char[] tag) {
		if (chars.length <= startIndex + tag.length)
			return -1;
		out:
		for (int i = startIndex; i < chars.length; i++) {
			for (int j = 0; j < tag.length; j++) {
				if (chars[i + j] != tag[j])
					continue out;
			}
			return i;
		}
		return -1;
	}
	
	private int getIndex(char[] chars, int startIndex, char c) {
		for (int i = startIndex; i < chars.length; i++) {
			if (chars[i] == c)
				return i;
		}
		return -1;
	}
}