package com.hhdb.csadmin.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hh.frame.csv.reader.CsvParser;
import com.hh.frame.csv.reader.CsvReader;
import com.hh.frame.csv.reader.CsvRow;
import com.hh.frame.csv.writer.CsvWriter;

public class CSVUtil {
	/**
	 * 将List<List<Object>>类型转换成cvs字符串类型
	 * @param dbtable
	 * @return
	 * @throws IOException 
	 */
	public static String List2CSV(List<List<Object>> list) throws IOException{

		Collection<String[]> data = new ArrayList<>();
		for(List<Object> l : list){
			String[] strvalue1 = new String[l.size()];
			for(int i=0;i<l.size();i++){
				strvalue1[i] = l.get(i)==null?"":l.get(i).toString();
			}
			data.add(strvalue1);
		}
		CsvWriter csvWriter = new CsvWriter();
		StringBuffer sbf = new StringBuffer();
    	csvWriter.writeSbf(sbf, data);
		return sbf.toString();
	}
	/**
	 * 将cvs字符串类型转换成List<List<String>>类型
	 * @param csvstr
	 * @return
	 * @throws IOException
	 */
	public static List<List<String>> cSV2List(String csvstr) throws IOException{
		List<List<String>> list = new ArrayList<List<String>>();	
		CsvReader csvReader = new CsvReader();
    	CsvParser csvParser;
		csvParser = csvReader.parseStr(csvstr);
		CsvRow row;
	    while ((row = csvParser.nextRow()) != null) {
	    	List<String> l = new ArrayList<String>();
	    	for(int i=0;i<row.getFieldCount();i++){
				l.add(row.getField(i));
			}
	    	list.add(l);
	    }
	    return list;
	}
}
