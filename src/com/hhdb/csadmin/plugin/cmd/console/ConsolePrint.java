package com.hhdb.csadmin.plugin.cmd.console;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.hh.frame.common.log.LM;

/**
 * 
 * <p>
 * Description: 字符sql窗口打印组件对象
 * </p>
 * <p>
 * Company: 恒辉
 * </p>
 * 
 * @author 张涛
 * @version 创建时间：2017年10月30日 上午11:06:25
 */
public class ConsolePrint extends JTextPane {
	private static final long serialVersionUID = 1L;
	public static enum SqlType{
		SELECT,UPDATE,INSERT,DELETE,OTHER
	}
	private static final String ENCODING = "GBK";
	private  List<Integer> maxLength;
	// 计时开关
	boolean timingFlag;
	// 耗时
	long time;
	// 滚动条
	JScrollBar scrollBar;
	ConsolePrint() {
	}
	
	//打印执行结果
	void printResponse(String str) {
		if(str!=null && !"".equals(str)){
			Document doc = getDocument();
			try {
				doc.insertString(doc.getLength(), str.toString(), null);
			} catch (BadLocationException e) {
				setText(e.getMessage());
				LM.error(LM.Model.CS.name(), e);
			}
			scrollBar.setValue(scrollBar.getMaximum());
		}
	}

	void clear() {
		setText(null);
	}
	
	// 执行计时开关
	void timing() {
		timingFlag = timingFlag ? false : true;
		if (timingFlag) {
			printResponse(ConsoleConstant.MSG_START_TIMING);
		} else {
			printResponse(ConsoleConstant.MSG_CLOSE_TIMING);
		}
	}

	// 打印执行耗时
	void printTiming() {
		if (time > 0 && timingFlag) {
			printResponse("耗时 " + time + " ms\n");
			time = 0;
		}
	}
	
	/**
	 * 打印更多行, 每次+1
	 * 
	 * @param result
	 * @param rowsIndex
	 * @return
	 */
	public void printMoreRow(List<List<Object>> list, int rowsIndex) {
		StringBuffer buffer = new StringBuffer();
		List<Object> row = list.get(rowsIndex+1);
		int size = row.size();
		String value = null;
		
		for (int j = 0; j < size; j++) {
			value = row.get(j)!=null?row.get(j).toString():"";
			try {
				printCell(maxLength, buffer, size, j, value);
			} catch (UnsupportedEncodingException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
		printResponse(buffer.append("\n").toString());
	}

	public void setMaxLength(List<List<Object>> list) {
		try {
			maxLength = getTableColWidth(list);
		} catch (UnsupportedEncodingException e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}

	/**
	 * 
	 * @param result
	 * @param rowsIndex
	 *            当前位置
	 * @param showRowCount
	 *            显示行数, 如果不够会自动显示最大行
	 * @return
	 */
	public void printResult(List<List<Object>> list,int rowConunt, int rowsIndex, int showRowCount) {
//		String command = result.getCommand();
//		if (command == null){
//			printResponse(printQuery(result.getDbTable(), rowsIndex, showRowCount));
//			return;
//		}
		String resultStr = null;
//		SqlType sqlType = null;
//		try {
//			sqlType = SqlType.valueOf(command);
//		} catch (IllegalArgumentException e) {
//			sqlType = SqlType.OTHER;
//		}
//		switch (sqlType) {
//		case SELECT:
//			resultStr = printQuery(result.getDbTable(), rowsIndex, showRowCount);
//			break;
//		case UPDATE:
//			resultStr = printUpdate(command, result.getRowCount());
//			break;
//		case INSERT:
//			resultStr = printUpdate(command, result.getRowCount());
//			break;
//		case DELETE:
//			resultStr = printUpdate(command, result.getRowCount());
//			break;
//		default:
//			resultStr = command + "\n";
//			break;
//		}
		if(list!=null){
			resultStr = printQuery(list, rowsIndex, showRowCount);
		}else{
			resultStr = rowConunt+"执行成功";
		}
		printResponse(resultStr);
	}

//	private String printQuery(DBTable dbTable, int rowsIndex, int showRowCount) {
//		List<Column> columns = dbTable.getColumns();
//		List<Map<String, Object>> rows = dbTable.getRows();
//		try {
//			return printTable(columns, rows, rowsIndex, showRowCount);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return e.getMessage();
//		}
//	}
	private String printQuery(List<List<Object>> list, int rowsIndex, int showRowCount) {
//		List<Column> columns = dbTable.getColumns();
//		List<Map<String, Object>> rows = dbTable.getRows();
		try {
			return printList(list, rowsIndex, showRowCount);
		} catch (UnsupportedEncodingException e) {
			LM.error(LM.Model.CS.name(), e);
			return e.getMessage();
		}
	}

//	private String printTable(List<Column> columns, List<Map<String, Object>> rows, int rowsIndex, int showRowCount) throws UnsupportedEncodingException {
//		maxLength = getTableColWidth(columns, rows);
//		StringBuffer buffer = new StringBuffer();
//
//		// 打印表结构
//		String value = null;
//		int size = columns.size();
//		if (size > 0) {
//			for (int i = 0; i < size; i++) {
//				value = columns.get(i).getColLabel();
//				printCell(maxLength, buffer, size, i, value);
//			}
//			buffer.append("\n");
//		}
//		// 打印分隔行
//		printLine(buffer, maxLength);
//
//		//打印部分
//		int remaining = rows.size() - rowsIndex;
//		int n = remaining > showRowCount ? rowsIndex + showRowCount : rows.size();
//		//打印全部
//		if (rowsIndex == 0 && showRowCount == 0)
//			n = rows.size();
//		// 打印数据
//		for (int i = rowsIndex; i < n; i++) {
//			for (int j = 0; j < columns.size(); j++) {
//				value = (String) rows.get(i).get(columns.get(j).getColLabel());
//				if (value == null)
//					value = "";
//				printCell(maxLength, buffer, size, j, value);
//			}
//			buffer.append("\n");
//		}
//		return buffer.toString();
//	}
	
	private String printList(List<List<Object>> list, int rowsIndex, int showRowCount) throws UnsupportedEncodingException {
		maxLength = getTableColWidth(list);
		StringBuffer buffer = new StringBuffer();

		// 打印表结构
		String value = null;
		int size = list.get(0).size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				value = list.get(0).get(i)!=null?list.get(0).get(i).toString():"";
				printCell(maxLength, buffer, size, i, value);
			}
			buffer.append("\n");
		}
		// 打印分隔行
		printLine(buffer, maxLength);

		//打印部分
		int remaining = list.size() - rowsIndex - 1;
		int n = remaining > showRowCount ? rowsIndex + showRowCount : list.size()-1;
		//打印全部
		if (rowsIndex == 0 && showRowCount == 0)
			n = list.size()-1;
		// 打印数据
		for (int i = rowsIndex; i < n; i++) {
			for (int j = 0; j < list.get(0).size(); j++) {
				value = list.get(i+1).get(j)!=null?list.get(i+1).get(j).toString():"";
				printCell(maxLength, buffer, size, j, value);
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	

//	private String printUpdate(String command, int count) {
//		return command + " " + count + "\n";
//	}

	/**
	 * 遍历所有数据, 取得字节最长的值作为列宽
	 * 
	 * @param columns
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private List<Integer> getTableColWidth(List<List<Object>> list) throws UnsupportedEncodingException {
		List<List<Integer>> allLength = new ArrayList<List<Integer>>();

		List<Integer> rowLength = null;
		for(List<Object> l:list){
			rowLength = new ArrayList<Integer>();
			for(Object obj :l){
				if (obj == null)
					rowLength.add(0);
				else
					rowLength.add(getStrLength(obj.toString()));
			}
			allLength.add(rowLength);
		}

		// 获得每一列最长的字段
		List<Integer> maxLength = new ArrayList<Integer>();
		for (int i = 0; i < list.get(0).size(); i++) {
			int max = allLength.get(0).get(i);
			for (int j = 1; j < allLength.size(); j++) {
				int temp = allLength.get(j).get(i);
				max = max > temp ? max : temp;
			}
			maxLength.add(max + 2); // 前后空格
		}
		return maxLength;
	}
//	/**
//	 * 遍历所有数据, 取得字节最长的值作为列宽
//	 * 
//	 * @param columns
//	 * @param rows
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	private List<Integer> getTableColWidth(List<Column> columns, List<Map<String, Object>> rows) throws UnsupportedEncodingException {
//		List<List<Integer>> allLength = new ArrayList<List<Integer>>();
//
//		// 表结构字段长度集合
//		List<Integer> structureLength = new ArrayList<Integer>();
//		for (int i = 0; i < columns.size(); i++) {
//			structureLength.add(getStrLength(columns.get(i).getColLabel()));
//		}
//		allLength.add(structureLength);
//
//		// 每条记录的字段长度集合
//		List<Integer> rowLength = null;
//		Map<String, Object> row = null;
//		for (int i = 0; i < rows.size(); i++) {
//			rowLength = new ArrayList<Integer>();
//			row = rows.get(i);
//			for (int j = 0; j < columns.size(); j++) {
//				String value = (String) row.get(columns.get(j).getColLabel());
//				if (value == null)
//					rowLength.add(0);
//				else
//					rowLength.add(getStrLength(value));
//			}
//			allLength.add(rowLength);
//		}
//		// 获得每一列最长的字段
//		List<Integer> maxLength = new ArrayList<Integer>();
//		for (int i = 0; i < columns.size(); i++) {
//			int max = allLength.get(0).get(i);
//			for (int j = 1; j < allLength.size(); j++) {
//				int temp = allLength.get(j).get(i);
//				max = max > temp ? max : temp;
//			}
//			maxLength.add(max + 2); // 前后空格
//		}
//		return maxLength;
//	}

	/**
	 * 打印单元格的空格和值
	 * 
	 * @param maxLength
	 * @param buffer
	 * @param size
	 * @param i
	 * @param value
	 * @throws UnsupportedEncodingException
	 */
	private void printCell(List<Integer> maxLength, StringBuffer buffer, int size, int i, String value) throws UnsupportedEncodingException {
		int fillLenth = maxLength.get(i) - getStrLength(value);
		value = value.trim();
		if (fillLenth % 2 == 0) {
			int c = fillLenth / 2;
			for (int j = 0; j < c; j++) {
				buffer.append(" ");
			}
			buffer.append(value);
			for (int j = 0; j < c; j++) {
				buffer.append(" ");
			}
		} else {
			int c = (fillLenth + 1) / 2;
			for (int j = 0; j < c - 1; j++) {
				buffer.append(" ");
			}
			buffer.append(value);
			for (int j = 0; j < c; j++) {
				buffer.append(" ");
			}
		}

		if (i + 1 < size)
			buffer.append("|");
	}

	/**
	 * 打印列名和结果集中的分割线
	 * 
	 * @param buffer
	 * @param tableColWidth
	 */
	private void printLine(StringBuffer buffer, List<Integer> tableColWidth) {
		int size = tableColWidth.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < tableColWidth.get(i); j++) {
				buffer.append("-");
			}
			if (i != size - 1) {
				buffer.append("+");
			}
		}
		if (tableColWidth.size() > 0)
			buffer.append("\n");
	}
	
	/**
	 * trim后计算字符串字节长度
	 * @param value 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private int getStrLength(String value) throws UnsupportedEncodingException{
		return value.trim().getBytes(ENCODING).length;
	}
}
