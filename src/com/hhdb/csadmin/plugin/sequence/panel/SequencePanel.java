package com.hhdb.csadmin.plugin.sequence.panel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.sequence.SequenceManager;
import com.hhdb.csadmin.plugin.sequence.common.BaseTabbedPaneCustom;
import com.hhdb.csadmin.plugin.sequence.common.BaseToolBar;
import com.hhdb.csadmin.plugin.sequence.component.BaseButton;



/**
 * 新建序列,tab页
 * @author gd
 *
 */
public class SequencePanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseToolBar toolBar = new BaseToolBar();
	private BaseTabbedPaneCustom tab;
    private SequenceGeneralPanel sequenceGeneralPanel= null;
    private SequenceAnnotationPannel sequenceAnnotationPannel=null;
	private boolean isedit;
	private String schemaName;
	private QueryTextPane sqlpanel;
	private String designsql="";
	private Map<String, String> map1=null;
	private SequenceManager seqm;
	private String seqName;
	/**
	 * 新增、设计序列
	 * 
	 * @param node
	 */
	public SequencePanel(QueryTextPane sqlpanel,String schemaName,String seqName,final boolean isEdit,SequenceManager seqm) {
	    this.isedit=isEdit;
	    this.sqlpanel=sqlpanel;
	    this.schemaName=schemaName;
	    this.seqName=seqName;
	    this.seqm=seqm;
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		tab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		sequenceGeneralPanel= new SequenceGeneralPanel(isEdit,seqName,schemaName,seqm);//常规
		sequenceAnnotationPannel=new SequenceAnnotationPannel();//注释 
		tab.addTab("常规", new JScrollPane(sequenceGeneralPanel));
		tab.addTab("注释", sequenceAnnotationPannel);
		tab.addTab("SQL预览", new JScrollPane(sqlpanel));
		//初始化工具栏
		initAddTool();
		if(isEdit){//设计序列时赋值
			    //注释赋值
			    setDescription(seqName);
				//设计师sql和界面控制
				controlSqlViewforDesign(seqName);
		}else{
			//新增sql和界面控制
			controlSqlViewforAdd();	
		}
			setLayout(new GridBagLayout());
			//工具条
			add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			//签页
			add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));	
	
	}
	

	/**
	 * 创建按钮
	 * @param text
	 * @param icon
	 * @param comand
	 */
	private void createBtn(String text, Icon icon, String comand) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		toolBar.add(basebtn);
	}
	/**
	 * 新增工具栏
	 */
	private void initAddTool(){
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();	
		toolBar.setFloatable(false);
	}
	/**
	 * 
	 * 新增Seq控制sql面板
	 *
	 */
	private void controlSqlViewforAdd() {
		tab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) { //响应鼠标点击事件
            	String seqname="NewSequence";
            	String sql =sequenceGeneralPanel.getParaForCreate(seqname);
            	String comment= sequenceAnnotationPannel.getText();
            	sql+="\n"+creatCommentSql(comment,seqname);//获取注释sql
            	sqlpanel.setText(sql);
            }	
        });
	}
	/**
	 * 设计时注释sql和注释界面控制
	 * @param seqName 
	 */
	private void controlSqlViewforDesign(final String seqName) {
		tab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) { //响应鼠标点击事件
            	 Map<String,Object> map =sequenceGeneralPanel.getParaForDesign(seqName);
            	 String updatesql=String.valueOf(map.get("updatesql"));
            	 String selectsql=String.valueOf(map.get("selectsql"));
            	 designsql=updatesql+selectsql;
             	String comment= sequenceAnnotationPannel.getText();
             	String description = map1.get("description");
             	if(!comment.equals(description)){
             		comment="\rCOMMENT ON SEQUENCE \""+schemaName+"\".\""+seqName+"\" IS"+"\r\'"+comment+"\';";
             		designsql+=comment;
             	}
                sqlpanel.setText(designsql);
            }
        });
	}

	   //根据序列名和模式名查询注释 赋值到 注释面板
		private void setDescription(String seqName){
		        String description = sequenceGeneralPanel.getDescription(seqName);
		         map1=new HashMap<>();
				 if(description==null){
					 map1.put("description", "");
				 }else{
					 map1.put("description", description);
					 sequenceAnnotationPannel.setText(description);	
				 }
		}

	
	/**
	 * @param seqName 
	 *注释
	 */
	private String creatCommentSql(String comment, String seqName) {
		if(!"".equals(comment)){
    		comment="\rCOMMENT ON SEQUENCE \""+schemaName+"\".\""+seqName+"\" IS"+"\r\'"+comment+"\';";
    	}
		return comment;
	}

	/**
	 * 保存动作控制
	 */
	public void actionPerformed(ActionEvent e) {
		//保存
		if (e.getActionCommand().equals("save")) {
			  Boolean savaflag;
			//添加序列
			if(!isedit){
				//得到序列名
				String sequenceName = JOptionPane.showInputDialog(null, "输入序列名", "序列名", JOptionPane.PLAIN_MESSAGE);
				if(sequenceName==null){
					return;
				}
				if(sequenceName.trim().equals("")){
					JOptionPane.showMessageDialog(null,"请输入序列名");
                   return;
				}
				sequenceName=sequenceName.trim();
				//得到注释
				String comment= sequenceAnnotationPannel.getText();
				//注释与 序列 拼接的
				String commentsql=creatCommentSql(comment, sequenceName);
				String createsql=sequenceGeneralPanel.getParaForCreate(sequenceName);
				createsql+=commentsql;
				savaflag=sequenceGeneralPanel.saveSeqForCreate(createsql);
				if(savaflag){
					sqlpanel.setText("");
					//再次点击为更新
					isedit=true;
					seqName=sequenceName;
					sequenceGeneralPanel.loadOldData();
					setDescription(sequenceName);
					controlSqlViewforDesign(sequenceName);
					//刷新树
					seqm.refreshTree(schemaName);
	        	}
			}
			//设计序列
			else{
				Map<String ,Object> mapsql=new HashMap<String ,Object>();
				mapsql=sequenceGeneralPanel.getParaForDesign(seqName);
				//的到注释
				String comment= sequenceAnnotationPannel.getText();
				String commentsql="";
				//判断现注释与原注释是否一样  不一样则更新
				if(!comment.equals(map1.get("description"))){
					//得到注释sql
					commentsql="\rCOMMENT ON SEQUENCE \""+schemaName+"\".\""+seqName+"\" IS"+"\r\'"+comment+"\';";
				}
				mapsql.put("commentsql",commentsql);
				savaflag=sequenceGeneralPanel.saveSeqForDesidn(mapsql);
				if(savaflag){
					sqlpanel.setText("");
					sequenceGeneralPanel.loadOldData();
					setDescription(seqName);
				}
			}
			
		}
		
	
	}

	
}
