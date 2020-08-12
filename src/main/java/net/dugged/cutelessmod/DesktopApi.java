package net.dugged.cutelessmod;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

// Stolen from mightypork from Stackoverflow.
// https://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform/18004334#18004334

public class DesktopApi {
	public static void browse(URI uri) {
		openSystemSpecific(uri.toString());
	}

	private static void openSystemSpecific(String what) {
		final EnumOS os = getOs();
		if (os.isLinux()) {
			if (!runCommand("kde-open", what) && !runCommand("gnome-open", what)) {
				runCommand("xdg-open", what);
			}
			return;
		}

		if (os.isMac()) {
			runCommand("open", what);
			return;
		}

		if (os.isWindows()) {
			runCommand("explorer", what);
		}

	}

	private static boolean runCommand(String command, String file) {
		final String[] parts = prepareCommand(command, file);
		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null) return false;

			try {
				final int retval = p.exitValue();
				if (retval == 0) {
					return false;
				} else {
					return false;
				}
			} catch (IllegalThreadStateException itse) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}


	private static String[] prepareCommand(String command, String file) {
		final List<String> parts = new ArrayList<>();
		parts.add(command);

		for (String s : "%s".split(" ")) {
			s = String.format(s, file); // put in the filename thing
			parts.add(s.trim());
		}

		return parts.toArray(new String[0]);
	}

	private static EnumOS getOs() {
		final String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return EnumOS.windows;
		}

		if (s.contains("mac")) {
			return EnumOS.macos;
		}

		if (s.contains("solaris")) {
			return EnumOS.solaris;
		}

		if (s.contains("sunos")) {
			return EnumOS.solaris;
		}

		if (s.contains("linux")) {
			return EnumOS.linux;
		}

		if (s.contains("unix")) {
			return EnumOS.linux;
		} else {
			return EnumOS.unknown;
		}
	}


	public enum EnumOS {
		linux, macos, solaris, unknown, windows;

		public boolean isLinux() {
			return this == linux || this == solaris;
		}

		public boolean isMac() {
			return this == macos;
		}

		public boolean isWindows() {
			return this == windows;
		}
	}
}