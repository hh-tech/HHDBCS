package com.hh.hhdb_admin.common;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.csv.CsvUtil;
import com.hh.frame.db_tabmg.util.DataTemplateUtil;
import com.hh.frame.db_tabmg.util.HiddenColUtil;
import com.hh.frame.db_tabmg.util.LobColUtil;
import com.hh.frame.db_tabmg.util.TypeCategoryUtil;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.comp.lob.LobJsonCol;

/**
 * table帮助类
 * 将查询出来file的数据内容转成表格数据
 * 更加查询类型转成生成表格列
 */
public class TabHelpTool {
	private DBTypeEnum dbtype;
	private Version ver;
	private String nullString;
	private List<Enum<?>> selTypes;
	private List<String> colNames;

	public TabHelpTool(DBTypeEnum dbtype, Version ver, String nullFlag, List<Enum<?>> types, List<String> cols)
			throws IOException, SQLException {
		this.dbtype = dbtype;
		this.ver = ver;
		this.nullString = nullFlag;
		this.selTypes = types;
		this.colNames = cols;
	}

	/**
	 * 转成table加载的数据格式
	 * 
	 * @param csvFile
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, String>> toDataMap(File csvFile) throws IOException {

		List<Map<String, String>> dataListMap = new ArrayList<>();
		if (csvFile == null || !csvFile.exists()) {
			return new ArrayList<>();
		}
		List<List<String>> csvData = CsvUtil.csv2Data(csvFile, nullString);
		String lobFilePath = csvFile.getAbsolutePath().replaceAll("csv", "dat");
		for (List<String> csvDatum : csvData) {
			LinkedHashMap<String, String> rowMap = new LinkedHashMap<>(16);
			for (int j = 0; j < selTypes.size(); j++) {
				String colName = colNames.get(j);
				if (rowMap.containsKey(colName)) {
					// 防止普通列名称相同的情况
					colName = colName + j;
				}
				String value = formatColValue(selTypes.get(j), colName, csvDatum.get(j), lobFilePath);
				rowMap.put(colName, value);
			}
			dataListMap.add(rowMap);
		}
		return dataListMap;
	}





	/**
	 * 创建表格列
	 *
	 * @return
	 */
	public List<AbsCol> createCol(File baseDir) {
		List<AbsCol> cols = new ArrayList<>();

		if (colNames == null || selTypes == null) {
			return cols;
		}
		Map<String, String> rowMap = new LinkedHashMap<>(16);
		for (int i = 0; i < colNames.size(); i++) {
			String colName = colNames.get(i);
			AbsCol col;
			// 导出到csv的类型
			Enum<?> type = selTypes.get(i);

			boolean isBlob = TypeCategoryUtil.isBlob(type);
			boolean isClob = TypeCategoryUtil.isClob(type);
			;
			boolean isHiddenCol = HiddenColUtil.isHiddenCol(dbtype, ver, colName);

//			System.out.println(colName + "--" + type);

			if ((isBlob || isClob) && !isHiddenCol) {
				col = new LobJsonCol(rowMap.containsKey(colName) ? colName + i : colName, colName, isBlob, type.name(),
						baseDir);
			} else {
				col = new DataCol(rowMap.containsKey(colName) ? colName + i : colName, colName, true);
			}

			cols.add(col);
			if (isHiddenCol) {
				col.setShow(false);
			}
			rowMap.put(colName, colName);
		}
		return cols;
	}
	
	/**
	 * 把value转成需要的格式
	 * 
	 * @param selType
	 * @param colName
	 * @param value
	 * @param lobFilePath
	 * @return
	 */
	private String formatColValue(Enum<?> selType, String colName, String value, String lobFilePath) {
		if (StringUtils.isEmpty(value) || nullString.equals(value)) {
			return null;
		}
		if (TypeCategoryUtil.isBlobOrClob(selType)
				&& !HiddenColUtil.isHiddenCol(dbtype, ver, colName)) {
			return LobColUtil.getLobJsonObj(selType.name(), value, lobFilePath).toString();
		}
		value = DataTemplateUtil.getFormat(dbtype, ver, selType.name(), colName, value, lobFilePath);
		return value;
	}
}
