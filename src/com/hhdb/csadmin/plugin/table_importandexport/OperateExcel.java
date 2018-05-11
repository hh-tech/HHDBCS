package com.hhdb.csadmin.plugin.table_importandexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.table_importandexport.util.ExportUtil;

/**
 * @createTime:2017年12月18日17:24:52
 * @remark:Excel导入导出
 * @author hwj
 * @version 1.0
 */
public class OperateExcel {

	/**
	 * Excel导出
	 * 
	 * @param event
	 * @return int 1成功 2失败
	 * @throws Exception
	 */
	public int exportExcle(HHEvent event) throws Exception {
		JFileChooser chooser = new JFileChooser();
		// 设置选择模式，既可以选择文件又可以选择文件夹
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XLS文件(*.xls,*xlsx)", "xls", "xlsx");
		chooser.setSelectedFile(new File(event.getPropMap().get("tableName")));
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		FileOutputStream fos = null;
		// 创建工作簿
		XSSFWorkbook xw = new XSSFWorkbook();
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			try {
				// 得到表数据
				List<List<Object>> dbTable = ExportUtil.inAndOutPutTable.getDBtable(event);
				// 得到表格所有列名
				List<Object> lables = dbTable.get(0);
				// 创建Excel表
				XSSFSheet xs = null;
				String[] strs = new String[lables.size()];
				// 如果没有表名则命名为sheet1，有则命名为数据库表名
				if ("".equals(event.getPropMap().get("tableName"))) {
					xs = xw.createSheet("sheet1");
				} else {
					xs = xw.createSheet(event.getPropMap().get("tableName"));
				}
				// 创建表格第1行 
				XSSFRow row = xs.createRow(0);
				for (int i = 0; i < lables.size(); i++) {
					strs[i] = lables.get(i).toString();
					// 第一行第一格
					XSSFCell cell = row.createCell(i);
					// 赋值
					cell.setCellValue(lables.get(i).toString());
				}
				for (int j = 1; j < dbTable.size(); j++) {
					// 依次添加数据库中查询到的数据到excel中
					List<Object> list = dbTable.get(j);
					XSSFRow rows = xs.createRow(j + 1);
					for (int k = 0; k < list.size(); k++) {
						XSSFCell cells = rows.createCell(k);
						if (list.get(k) == null) {
							cells.setCellValue("");
						}
						cells.setCellValue(list.get(k) + "");
					}
				}
				File file = new File(chooser.getSelectedFile().getCanonicalPath()
						+ ".xlsx");
				fos = new FileOutputStream(file);
				xw.write(fos);
				return 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (xw != null) {
					xw.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
		}
		return 2;
	}

	/**
	 * Excel导入
	 * 
	 * @param event
	 * @return int 1成功 2失败
	 * @throws Exception
	 */
	public int importExcel(HHEvent event) throws Exception {
		JFileChooser chooser = new JFileChooser();
		// 设置选择模式，既可以选择文件又可以选择文件夹
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XLS文件(*.xls,*xlsx)", "xls", "xlsx");
		chooser.setSelectedFile(new File(event.getPropMap().get("tableName")));
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			// 得到表数据
			List<List<Object>> dbTable = ExportUtil.inAndOutPutTable.getDBtable(event);
			String tableName = event.getPropMap().get("tableName");
			// 得到表列名集合
			List<Object> LableList = dbTable.get(0);
			final File file = chooser.getSelectedFile();
			Workbook wookbook = Workbook.getWorkbook(file);
			// 统计excel的行数
			Sheet sheet = wookbook.getSheet(0);
			// excel总行数，记录数=行数-1
			int rowLen = sheet.getRows();
			Cell[] head = null;
			if (rowLen > 1) {
				// 获取第一行表头
				head = sheet.getRow(0);
				List<String> excelLable = new ArrayList<String>();
				// 数组中的表头添加到List集合中
				for (int i = 0; i < head.length; i++) {
					excelLable.add(head[i].getContents());
				}
				// 判断两个集合内容是否完全一致
				if (LableList.size() == excelLable.size()
						&& LableList.containsAll(excelLable)) {
					StringBuffer sql = new StringBuffer("INSERT INTO " + tableName + " VALUES ");
					StringBuffer contents = new StringBuffer("");
					for (int i = 1; i < rowLen; i++) {
						// 获取第二行内容
						Cell[] row = sheet.getRow(i);
						// 将Cell数组转换成List，返回的List类型是java.util.Arrays$ArrayList
						List<Cell> list = Arrays.asList(row);
						// new一个ArrayList集合
						List<Cell> cellList = new ArrayList<Cell>();
						// 将java.util.Arrays$ArrayList类型里的值放入ArrayList类型中，因为前者类型不支持List的增删操作
						for (int n = 0; n < list.size(); n++) {
							cellList.add(list.get(n));
						}
						// 如果cellList集合内的元素小于列的长度，说明该行至少最后一列是空值
						if (cellList.size() < LableList.size()) {
							// 得到空值的个数
							int lack = LableList.size() - cellList.size();
							// 将ArrayList集合扩容，扩容的大小是空值的大小，并且将值设置为null
							for (int l = 0; l < lack; l++) {
								cellList.add(null);
							}
						}
						if (cellList != null) {
							contents.append("(");
							for (int j = 0; j < head.length; j++) {
								if (cellList.get(j) == null
										|| cellList.get(j).getContents()
												.equals("")) {
									contents.append("null");
								} else {
									contents.append("'");
											contents.append(cellList.get(j).getContents()
													.replace("'", "''") + "'");
								}
								if ((j + 1) != cellList.size()) {
									contents.append(",");
								}
							}
							contents.append(")");
							if ((i + 1) != rowLen) {
								contents.append(",");
							}
						}
					}
					// 拼接sql
					sql.append(contents);
					String fromID = event.getToID();
					String toID = "com.hhdb.csadmin.plugin.conn";
					HHEvent sendSqlEvent = new HHEvent(fromID, toID,
							"ExecuteUpdateBySqlEvent");
					sendSqlEvent.addProp("sql_str", sql.toString());
					// 执行sql
					ExportUtil.inAndOutPutTable.sendEvent(sendSqlEvent);
					return 1;
				} else {
					throw new Exception("表头格式错误!");
				}
			} else {
				throw new Exception("数据不能为空,且必须带有表头!");
			}
		}
		return 2;
	}
}
