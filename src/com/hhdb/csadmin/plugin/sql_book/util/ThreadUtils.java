package com.hhdb.csadmin.plugin.sql_book.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public final class ThreadUtils {
	public static void invokeLater(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(runnable);
		} else {
			runnable.run();
		}
	}

	public static void invokeAndWait(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				// System.err.println("Not EDT");
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				// nothing to do here
			} catch (InvocationTargetException e) {
				// nothing to do here
			}
		} else {
			runnable.run();
		}
	}

	private ThreadUtils() {
	}
	
}
