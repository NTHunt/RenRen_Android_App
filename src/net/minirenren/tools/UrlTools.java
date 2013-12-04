<<<<<<< HEAD
package net.minirenren.tools;

import android.os.Bundle;

public class UrlTools {
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(key + "=" + parameters.getString(key));
		}
		return sb.toString();
	}
}
=======
package net.minirenren.tools;

import android.os.Bundle;

public class UrlTools {
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(key + "=" + parameters.getString(key));
		}
		return sb.toString();
	}
}
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
