package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.pack.OracleCompileTool;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

/**
 * @author YuSai
 */
public class CompileHandler extends AbsHandler {

	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		Connection conn = loginBean.getConn();
		String schemaName = getSchemaName();
		TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
		OraSessionEnum sessionEnum;
		OracleCSCompileTool tool;
		String objName = treeNode.getName();
		switch (nodeType) {
		case FUNCTION:
			sessionEnum = OraSessionEnum.function;
			break;
		case PROCEDURE:
			sessionEnum = OraSessionEnum.procedure;
			break;
		case PACKAGE_HEAD:
			sessionEnum = OraSessionEnum.packhead;
			objName = treeNode.getParentHTreeNode().getName();
			break;
		case PACKAGE_BODY:
			sessionEnum = OraSessionEnum.packbody;
			objName = treeNode.getParentHTreeNode().getName();
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + nodeType);
		}
		tool = new OracleCSCompileTool(conn, schemaName, sessionEnum, objName);
		tool.compile(StartUtil.parentFrame);
	}

	class OracleCSCompileTool extends OracleCompileTool {

		public OracleCSCompileTool(Connection conn, String schema, OraSessionEnum objType, String objName) {
			super(conn, schema, objType, objName);
		}

		public void compile(HFrame parent) {
			compile();
			String errorMsg = getErrorMsg(false);
			if (StringUtils.isEmpty(errorMsg)) {
				PopPaneUtil.info(parent.getWindow(), getLang("compileSuccess"));
			} else {
				HDialog dialog = new HDialog(parent, 800, 500);
				HPanel panel = new HPanel();
				HTextArea textArea = new HTextArea(false, false);
				textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);
				textArea.getArea().setWordWrap(true);
				textArea.setText(errorMsg);
				LastPanel lastPanel = new LastPanel();
				lastPanel.set(textArea.getComp());
				panel.setLastPanel(lastPanel);
				dialog.setRootPanel(panel);
				dialog.setWindowTitle(getLang("compileFailed"));
				dialog.setIconImage(IconFileUtil.getLogo());
				dialog.show();
			}
		}
	}
}
