package com.hhdb.csadmin.plugin.sequence.panel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hh.frame.dbobj.bean.SeqBean;
import com.hhdb.csadmin.plugin.sequence.SequenceManager;
import com.hhdb.csadmin.plugin.sequence.component.BaseLabel;
import com.hhdb.csadmin.plugin.sequence.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.sequence.utils.SequenceDao;
public class SequenceGeneralPanel extends JPanel {

	/**
	 * @author bingyan cao 常规面板
	 */
	private static final long serialVersionUID = 1L;
	private boolean isedit;
	private SequenceDao sequenceDao ;
	JTextField increase = new JTextField();
	JTextField presentValue = new JTextField();// 现值
	JTextField min = new JTextField();
	JTextField max = new JTextField();
	JTextField catchs = new JTextField();
	JCheckBox checkCycle = new JCheckBox("循环");// 循环(复选框)
	JCheckBox checkOwner = new JCheckBox("表列拥有者");// 添加拥有者
	ComboBoxCellEditor tableOwner = new ComboBoxCellEditor();
	ComboBoxCellEditor columnOwner = new ComboBoxCellEditor();
	BaseLabel tableLable = new BaseLabel("由表拥有:");
	BaseLabel columnLable = new BaseLabel("由列拥有:");
	private Map<String, Object> oldData = new HashMap<String, Object>();
    private String schemaName ;
    private String seqName ;
   
	// 鼠标单击事件
	MouseListener mouseListener = new MouseListener() {
		public void mouseClicked(MouseEvent e) {

			if (checkOwner.isSelected()) {

				showOwnerComponent();
			} else {
				closeOwnerComponent();
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
		
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

	};

	public SequenceGeneralPanel(final boolean isEdit,String seqName,String schemaName,SequenceManager seqm) {
		this.isedit = isEdit;
		this.schemaName=schemaName;
		this.seqName=seqName;
		this.sequenceDao=new SequenceDao(seqm);
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		ComponentPosition();
		tableOwnerSet();
		checkOwner.addMouseListener(mouseListener);// 添加事件
		tableOwner.addActionListener(tableOwnerEvent());
		setComponent();
		if(isEdit){
			setComponentValue();
		}

	}

	/**
	 * 组件位置放置
	 */
	public void setComponent() {
		// 测试添加拥有者选项
		// 递增
		add(new BaseLabel("递增:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(increase, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		// 现值
		add(new BaseLabel("现值:"), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(presentValue, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		// 最小
		add(new BaseLabel("最小:"), new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(min, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 30, 0, 0), 0, 0));
		// 最大
		add(new BaseLabel("最大:"), new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(max, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 30, 0, 0), 0, 0));
		// 缓存
		add(new BaseLabel("缓存:"), new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(catchs, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 30, 0, 0), 0, 0));
		// 循环
		add(checkCycle, new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
		// 添加拥有者
		add(checkOwner, new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
		// 由表拥有
		add(tableLable, new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tableOwner, new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));

		if (!isedit) {
			increase.setText("1");
			presentValue.setText("1");
			min.setText("1");
			max.setText("9223372036854775807");
			catchs.setText("1");
		}
		// 由表拥有
		add(columnLable, new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(columnOwner, new GridBagConstraints(1, 17, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 20, 4, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * 组件位置
	 */
	private void ComponentPosition() {

		increase.setPreferredSize(new Dimension(200, 20));
		presentValue.setPreferredSize(new Dimension(200, 20));
		min.setPreferredSize(new Dimension(200, 20));// 最小
		max.setPreferredSize(new Dimension(200, 20)); // 最大
		catchs.setPreferredSize(new Dimension(200, 20));// 缓存
		tableLable.setEnabled(false);
		columnLable.setEnabled(false);
		columnOwner.setEditable(false);
		columnOwner.setEnabled(false);
		tableOwner.setEnabled(false);
		tableOwner.setEditable(false);
		columnOwner.setPreferredSize(new Dimension(200, 20));
		tableOwner.setPreferredSize(new Dimension(200, 20));
	}

	
	/*
	 * 根据模式或得所有的表
	 * 
	 */
	private void tableOwnerSet()  {
		tableOwner.removeAllItems();//移除所有项
		 Set<String> tables = sequenceDao.getTables(schemaName);
		tableOwner.addItem("");
		for (String tableName : tables) {
			tableOwner.addItem(tableName);
		}
		
	}
  
	
	/**
	 *表监听
	 * @return
	 */
	private ActionListener tableOwnerEvent() {
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				columnOwner.removeAllItems();
				String tableName=tableOwner.getStringValue();
				if(!tableName.equals("")){
					List<String> columnList = sequenceDao.getColumnList(schemaName, tableName);
					for(String columnName:columnList){
						columnOwner.addItem(columnName);
					}
				}else{
					columnOwner.addItem("");
				}
					
			}
		};
		return listener;
	}
	/*
	 * 添加拼接SQL
	 */
	public String  getParaForCreate(String seqName){
		String INCREMENT= String.valueOf(increase.getText());//2递增
		String START= String.valueOf(presentValue.getText());//3现值
		String MINVALUE= String.valueOf(min.getText());//4最小值
		String MAXVALUE= String.valueOf(max.getText());//5最大值
		String CACHE= String.valueOf(catchs.getText());//6缓存
		Boolean CYCLE=checkCycle.isSelected();//7循环
		Boolean addOwner=checkOwner.isSelected();//8添加拥有者
		String tableName= String.valueOf(tableOwner.getSelectedItem());//9由表拥有
		String column= String.valueOf(columnOwner.getSelectedItem());//0由列拥有
		String start="CREATE SEQUENCE \""+schemaName+"\".\""+seqName+"\"";
		String start1="";
		if(!"".equals(INCREMENT)){ start1="\r INCREMENT  "+INCREMENT;}
		String start2="";
		if(!"".equals(MINVALUE)){start2="\r MINVALUE  "+MINVALUE;}
		String start3="";
		if(!"".equals(MAXVALUE)){start3="\r MAXVALUE  "+MAXVALUE;}
		String start4="";
		if(!"".equals(START)){start4="\r START  "+START;}
		String start5="";
		if(!"".equals(CACHE)){ start5="\r CACHE  "+CACHE;}
		String start6="";
		if(CYCLE){ start6="\r CYCLE";}
		String start7="";
		if(!"".equals(tableName)&&"true".equals(addOwner+""))
		{ 
			start7="\r OWNED BY \"" +schemaName+"\".\""
				+tableName+"\".\""+column+"\"";
		}
		String sqlSeparate=";" ;
		String end="";
		String sql=start+start1+start2+start3+start4+start5+start6+start7+sqlSeparate+end;
		return sql;
	}
	
	/**
	 * 保存序列
	 * @param comment 
	 * @return 
	 */
	public Boolean saveSeqForCreate(String createsql){
		
		return sequenceDao.saveSeq(createsql);
	}
	
	
	/**
	 * 设计时拼sql
	 * @param seqName
	 * @return
	 */
	public Map<String,Object> getParaForDesign(String seqName) {
		//获取原数据
		String oldIncrease=String.valueOf(oldData.get("increase"));
		String oldPresentValue=String.valueOf(oldData.get("presentValue"));
		String oldMin=String.valueOf(oldData.get("min"));
		String oldMax=String.valueOf(oldData.get("max"));
		String oldCatchs=String.valueOf(oldData.get("catchs"));
		Boolean oldCYCLE= (Boolean) oldData.get("checkCycle");
		String oldTableOwner=String.valueOf(oldData.get("tableOwner"));
		String oldColumnOwner=String.valueOf(oldData.get("columnOwner"));
		//获取界面值
		String INCREMENT= String.valueOf(increase.getText());//2递增
		String START= String.valueOf(presentValue.getText());//3现值
		String MINVALUE= String.valueOf(min.getText());//4最小值
		String MAXVALUE= String.valueOf(max.getText());//5最大值
		String CACHE= String.valueOf(catchs.getText());//6缓存
		Boolean CYCLE=checkCycle.isSelected();//7循环
		Boolean addOwner=checkOwner.isSelected();//8添加拥有者
		String tableName= String.valueOf(tableOwner.getSelectedItem());//9由表拥有
		String column= String.valueOf(columnOwner.getSelectedItem());//0由列拥有
		String sql1="";
		String sql2="";
		String sql3="";
		boolean flag1=!(CYCLE==oldCYCLE);
		boolean flag2=addOwner&&(!oldTableOwner.equals(tableName)||!oldColumnOwner.equals(column));
		if(!oldIncrease.equals(INCREMENT)||!oldPresentValue.equals(START)||!oldMin.equals(MINVALUE)||!oldMax.equals(MAXVALUE)||
			!oldCatchs.equals(CACHE)||flag1||flag2){
			sql1="ALTER SEQUENCE \""+schemaName+"\".\""+seqName+"\"";
		}
		if(!oldIncrease.equals(INCREMENT)){
			
			sql1+="\r INCREMENT "+INCREMENT;
		}
		
		if(!oldMin.equals(MINVALUE)){
			sql1+="\r MINVALUE "+MINVALUE;
			
		}
		//MAXVALUE
		if(!oldMax.equals(MAXVALUE)){
			sql1+="\r MAXVALUE "+MAXVALUE;
			
		}
		//当最大最小值改变时显示现值
		if(!oldPresentValue.equals(START)&&(!oldMin.equals(MINVALUE)||!oldMax.equals(MAXVALUE))){
			sql3="\r SELECT setval('\""+schemaName+"\".\""+seqName+"\"\',"+START+",false);";
			
			sql1+="\r START "+START;
		}
		if(oldPresentValue.equals(START)&&(!oldMin.equals(MINVALUE)||!oldMax.equals(MAXVALUE))){
			
			
			sql1+="\r START "+START;
		}
		// CACHE
		if(!oldCatchs.equals(CACHE)){
			sql1+="\r CACHE "+CACHE;
			
		}
		//CYCLE
		
		if(flag1){
			if(CYCLE){
				sql1+="\r CYCLE ";
			}else{
				sql1+="\r NO CYCLE";
			}
		}
		if(addOwner){
			if(!oldTableOwner.equals(tableName)||!oldColumnOwner.equals(column)){
				if("".equals(tableName)){
					sql1+="\r OWNED  BY NONE";
				}
				else{
				sql1+="\r OWNED  BY \""+schemaName+"\".\""+tableName+"\".\""+column+"\"";
				}
			}	
		}		
			
		//仅现值改变 最大值最小值没变
		if(!oldPresentValue.equals(START)&&(oldMin.equals(MINVALUE)&&oldMax.equals(MAXVALUE))){
			sql3="\r SELECT setval('\""+schemaName+"\".\""+seqName+"\"\',"+START+",false);";
			sql1="";
		}
	
		if(!"".equals(sql1)){
			sql1+=";";
		}
		 String sql=sql1+sql2;
		 Map<String,Object> map =new HashMap<String,Object>();
		 map.put("updatesql", sql);
		 map.put("selectsql", sql3);
		 return map;
	}
	
	//加载原数据
	public void loadOldData(){
		   //整合原数据
	       oldData.put("increase", increase.getText());
	       oldData.put("presentValue", presentValue.getText());
	       oldData.put("min", min.getText());
	       oldData.put("max", max.getText());
	       oldData.put("catchs", catchs.getText());
	       oldData.put("checkCycle",checkCycle.isSelected());
	       oldData.put("checkOwner",checkOwner.isSelected());
	       oldData.put("tableOwner",tableOwner.getSelectedItem());
	       oldData.put("columnOwner",columnOwner.getSelectedItem());
	}
	/*
	 * 设计序列赋值
	 * 
	 */
	public void setComponentValue(){
	        //根据模式名和序列名查询序列信息
	       SeqBean seqBean = sequenceDao.getSequenceInfo(schemaName, seqName);
	        //序列 表列拥有者
	        Map<String, String> map4 = sequenceDao.getSeqTnameandCname(schemaName, seqName);
	        String column="";
	        String tableName="";
	       if(!(map4==null||map4.size()==0)){
	        	 column=map4.get("column_name");
	        	 tableName=map4.get("table_name");
	        }
	        String INCREMENT=seqBean.getIncrement()+"";
	        String presentvalue=seqBean.getPresentValue()+"";
	        String MINVALUE=seqBean.getMin()+"";
	        String MAXVALUE=seqBean.getMax()+"";
	        String CACHE=seqBean.getCache()+"";
	        boolean CYCLE=seqBean.isCycle();
	        if(tableName!=null&&!tableName.equals("")){
	        	checkOwner.setSelected(true);
	        	//显示添加拥有者
	        	 showOwnerComponent();
	        }else{
	        	checkOwner.setSelected(false);
	        	columnOwner.addItem(column);
	        }
	        tableOwner.setSelectedItem(tableName);
	        columnOwner.setSelectedItem(column);
	        //赋值
	        increase.setText(INCREMENT);
	        min.setText(MINVALUE);
	        presentValue.setText(presentvalue);
	        max.setText(MAXVALUE);
	        catchs.setText(CACHE);
	        checkCycle.setSelected(CYCLE);
	       
	        //整合原数据
	       oldData.put("increase", INCREMENT);
	       oldData.put("presentValue", presentvalue);
	       oldData.put("min", MINVALUE);
	       oldData.put("max", MAXVALUE);
	       oldData.put("catchs", CACHE);
	       oldData.put("checkCycle",CYCLE);
	       oldData.put("tableOwner", tableName);
		   oldData.put("columnOwner", column);
	}
	
	/**
	 * 显示添加表列拥有者
	 */
	private void showOwnerComponent() {
		tableLable.setEnabled(true);
	    columnLable.setEnabled(true);
	    tableOwner.setEnabled(true);
	    columnOwner.setEnabled(true);
	}
	/**
	 * 没选中者表列拥有者
	 */
	private void closeOwnerComponent() {
		tableOwner.setEnabled(false);
		columnOwner.setEnabled(false);
		tableLable.setEnabled(false);
		columnLable.setEnabled(false);
	}
	/**
	 * 设计序列时保存
	 * @param designsql
	 * @param mapsql 
	 * @return
	 */
	public Boolean saveSeqForDesidn(Map<String, Object> mapsql) {
			String updatesql=String.valueOf(mapsql.get("updatesql"));
			String selectsql=String.valueOf(mapsql.get("selectsql"));
			String commentsql=String.valueOf(mapsql.get("commentsql"));
			String designsql=updatesql+commentsql;
			return sequenceDao.updateSeq(designsql, selectsql);
	 }
	
	/*
	 * 查询注释
	 * 
	 *
	 */
	public String getDescription(String seqName){
		
	        return sequenceDao.getDescription(seqName, schemaName);
		 
		 
	}
}
