package com.hhdb.csadmin.plugin.query.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hh.frame.common.util.db.SqlQueryUtil;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @ClassName: ExportFileService
 * @author: qinsz
 * @Description: 处理查询数据导出的业务逻辑
 * @date: 2017年11月6日 下午5:04:00
 */
public class ExportFileService {
	// 导出excel文件
	public static void exportExcelFile_current(JTable table) throws Exception {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XLS文件(*.xls,*xlsx)", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			File file = new File(chooser.getSelectedFile().getCanonicalPath()
					+ ".xls");
			OutputStream os = new FileOutputStream(file);
			createExcel_current(os, table);
		}
	}

	// 导出Excel
	private static void createExcel_current(OutputStream os, JTable table)
			throws Exception {
		// 创建工作薄
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		// 创建新的一页
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);
		int rows = table.getRowCount();
		int columns = table.getColumnCount();
		for (int j = 0; j < columns; j++) {
			table.getColumnName(j);
		}
		// 第一行标题
		for (int j = 0; j < columns; j++) {
			Label labelName = new Label(j, 0, (String) table.getColumnName(j));
			sheet.addCell(labelName);
		}
		// 创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Label label = new Label(j, i + 1, (String) table.getValueAt(i,
						j));
				sheet.addCell(label);
			}
		}
		// 把创建的内容写入到输出流中，并关闭输出流
		workbook.write();
		workbook.close();
		os.close();
	}

	public static void createExcel_all(Connection conn, String sql)
			throws Exception {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XLS文件(*.xls,*xlsx)", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			File file = new File(chooser.getSelectedFile().getCanonicalPath()
					+ ".xls");
			OutputStream os = new FileOutputStream(file);
			Vector<String> colname = new Vector<String>();
			List<List<String>> list = SqlQueryUtil.selectStrList(conn, sql);
			// Result rs=statement.executeQuery(sql);
			// DBTable dbtable=rs.getDbTable();
			for (String cn : list.get(0)) {
				colname.add(cn);
			}
			// List<Map<String, Object>> maps =
			// statement.executeQuery(sql).getRows();
			// 创建工作薄
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// 创建新的一页
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);
			for (int i = 0; i < colname.size(); i++) {
				Label labelName = new Label(i, 0, colname.get(i));
				sheet.addCell(labelName);
			}
			int maxRowCount = 60000;// 不能够超过Excel的最大容量
			for (int i = 1; i < list.size(); i++) {
				List<String> ll = list.get(i);
				for (int j=0;j<ll.size();j++) {
					Label label = new Label(j, i, ll.get(j));
					sheet.addCell(label);
				}
				if (i % maxRowCount == 0) {
					break;
				}
			}
			// 把创建的内容写入到输出流中，并关闭输出流
			workbook.write();
			workbook.close();
			os.close();
		}

	}

	public static void CSVWriter(Connection conn, String sql) throws Exception {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"CSV文件(*.csv)", "csv");
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			File file = new File(chooser.getSelectedFile().getCanonicalPath()
					+ ".csv");
			Vector<String> colname = new Vector<String>();
			// Result rs=statement.executeQuery(sql);
			// DBTable dbtable=rs.getDbTable();
			List<List<String>> list = SqlQueryUtil.selectStrList(conn, sql);
			for (String cn : list.get(0)) {
				colname.add(cn);
			}
			// List<Map<String, Object>> maps =
			// statement.executeQuery(sql).getRows();
			Writer writer = new FileWriter(file);
			CSVWriter csvWriter = new CSVWriter(writer, ',');
			String[] strs = new String[colname.size()];
			for (int i = 0; i < colname.size(); i++) {
				strs[i] = colname.get(i);
			}
			csvWriter.writeNext(strs);
			String[] str = null;
			for (int i = 1; i < list.size(); i++) {
				str = new String[colname.size()];
				List<String> ll = list.get(i);
				for (int j=0;j<ll.size();j++) {
					str[j] = ll.get(j);
				}
				csvWriter.writeNext(str);
			}
			csvWriter.close();
		}
	}

}
