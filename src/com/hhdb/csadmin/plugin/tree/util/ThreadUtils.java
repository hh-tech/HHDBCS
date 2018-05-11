package com.hhdb.csadmin.plugin.tree.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.hh.frame.common.log.LM;

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
				LM.error(LM.Model.CS.name(), e);
				// nothing to do here
			} catch (InvocationTargetException e) {
				LM.error(LM.Model.CS.name(), e);
				// nothing to do here
			}
		} else {
			runnable.run();
		}
	}

	private ThreadUtils() {
	}
	
}
