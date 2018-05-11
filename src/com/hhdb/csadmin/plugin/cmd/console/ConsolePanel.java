package com.hhdb.csadmin.plugin.cmd.console;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;

import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.plugin.cmd.SqlCmdPlugin;

/**
 * 命令行面板, 继承JPanel, 其中包含一个滚动面板
 * 
 * @author 钟苇
 * @version 2016年4月26日
 */
public class ConsolePanel extends JPanel {
	private static final long serialVersionUID = -2143266201963594817L;

	private Connection connection;

	public ConsolePanel(Connection connection,SqlCmdPlugin sqlcmdplugin) {
		this.connection = connection;
		this.setLayout(new GridBagLayout());
		this.add(new ConsoleScrollPanel(connection,sqlcmdplugin), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
	}

	public void close() {
		try {
			connection.close();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}

}
