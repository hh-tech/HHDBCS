package com.hhdb.csadmin.plugin.table_importandexport;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hh.frame.csv.reader.CsvParser;
import com.hh.frame.csv.reader.CsvReader;
import com.hh.frame.csv.reader.CsvRow;
import com.hh.frame.csv.writer.CsvWriter;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.table_importandexport.util.ExportUtil;

/**
 * @createTime:2017年12月18日16:58:52
 * @remark:CSV导入导出
 * @author hwj
 * @version 1.0
 */
public class OperateCsv {
	/**
	 * 表格导出csv
	 * 
	 * @param event
	 * @return int 1成功 2失败
	 * @throws Exception
	 */
	public static int ExportCSV(HHEvent event) throws Exception {
		// 创建选择文件面板
		JFileChooser chooser = new JFileChooser();
		// 设置选择模式，既可以选择文件又可以选择文件夹
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"CSV文件(*.csv)", "csv");
		chooser.setFileFilter(filter);
		int resultFile = chooser.showSaveDialog(null);
		if (resultFile == JFileChooser.APPROVE_OPTION) {
			// 导出的位置
			File file = new File(chooser.getSelectedFile().getCanonicalPath()
					+ ".csv");
			// 得到表数据
			List<List<Object>> dbTable = ExportUtil.inAndOutPutTable.getDBtable(event);
			// 得到表格所有列名
			List<Object> lable = dbTable.get(0);
			// CSV格式导出工具类
			CsvWriter csvWriter = new CsvWriter();
			// 将数据从数据库查出，把表头放入集合
			String[] lables = lable.toArray(new String[lable.size()]);
			final Collection<String[]> data = new ArrayList<>();
			data.add(lables);
			// 循环将数据库内一条记录放入字符串数组
			for (int i = 1; i < dbTable.size(); i++) {
				List<Object> list = dbTable.get(i);
				String[] values = new String[lables.length];
				for (int j = 0; j < list.size(); j++) {
					values[j] = list.get(j) == null ? null : list.get(j).toString();
				}
				data.add(values);
			}
			// 写出
			csvWriter.write(file, StandardCharsets.UTF_8, data);
			return 1;
		}
		return 2;
	}

	/**
	 * 表格导入csv
	 * 
	 * @param event
	 * @return int 1成功 2失败
	 * @throws Exception
	 */
	public static int ImportCSV(HHEvent event) throws Exception {
		// 创建选择文件面板
		JFileChooser chooser = new JFileChooser();
		// 设置选择模式，既可以选择文件又可以选择文件夹
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"CSV文件(*.csv)", "csv");
		chooser.setFileFilter(filter);
		int results = chooser.showOpenDialog(null); // 打开"打开文件"对话框
		if (results == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			CsvReader csvReader = new CsvReader();
			CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8);
			// 得到表数据
			List<List<Object>> dbTable = ExportUtil.inAndOutPutTable.getDBtable(event);
			// 得到表列名集合
			List<Object> LableList = dbTable.get(0);
			// 获得第一行数据
			CsvRow row = csvParser.nextRow();
			List<String> csvLables = new ArrayList<String>();
			// 循环将数据放到集合内
			for (int i = 0; i < row.getFieldCount(); i++) {
				csvLables.add(row.getField(i));
			}
			// 如果表格内的第一行是表头且与数据库的表头列数完全一致则开始导入
			if (LableList.size() == csvLables.size()
					&& LableList.containsAll(csvLables)) {
				String tableName = event.getPropMap().get("tableName");
				StringBuffer sbsql = new StringBuffer("INSERT INTO "
						+ tableName + " VALUES ");
				StringBuffer contents = new StringBuffer("");
				// sql的拼接
				while ((row = csvParser.nextRow()) != null) {
					contents.append("(");
					int i;
					for (i = 0; i < csvLables.size(); i++) {
						if (row.getField(i) == null
								|| row.getField(i).equals("")) {
							contents.append("null");
						} else {
							contents.append("'"
									+ row.getField(i).replace("'", "''") + "'");
						}
						if ((i + 1) != csvLables.size()) {
							contents.append(",");
						} else {
							contents.append("),");
						}
					}
				}
				String errorsql = sbsql.append(contents).toString();
				// 最后会多出一个逗号，所以截取之前的字符串
				String sql = errorsql.substring(0, errorsql.length() - 1);
				String fromID = event.getToID();
				String toID = "com.hhdb.csadmin.plugin.conn";
				CmdEvent sendSqlEvent = new CmdEvent(fromID, toID,
						"ExecuteUpdateBySqlEvent");
				sendSqlEvent.addProp("sql_str", sql.toString());
				// 执行sql
				ExportUtil.inAndOutPutTable.sendEvent(sendSqlEvent);
				csvParser.close();
				return 1;
			} else {
				throw new Exception("表头格式错误!");
			}
		}
		return 2;
	}
}
