package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.base.SshBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.EnumUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.cs_desk.SshPanel;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaIconUtil;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;
import com.hh.hhdb_admin.comp.DBTypeComboBox;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class LoginTabComp  {
	public static final String TABID = "general";
	private static final String NAME = "name";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String SCHEMA = "schema";
	private static final String USER_TYPE = "userType";

	private static final String ENABLED = "enabled";
	private HTabPanel tabPane;
	private SshPanel sshPanel;
	
	public TextInput nameInput,schemaInput,usernameInput;
	private SelectBox viewInput;
	private DBTypeComboBox typeInput;
	private TextInputExt connectInput;
	private PasswordLockInput passwordInput;
	private CheckBoxInput userTypeInput,enabledInput,autoSaveInput;
	
	private DirChooserInput chooseInput;
	private JdbcInfo jdbcInfo; //当前连接信息
//	private String DEFAULT = new File(CsVarUtil.getEtcFile(),CsVarUtil.BTN_SQL).getAbsolutePath();
	public LoginTabComp() {
		this.tabPane = new HTabPanel();
		LastPanel last = new LastPanel();
		HBarLayout layout = new HBarLayout();
		layout.setAlign(AlignEnum.LEFT);
		layout.setBottomHeight(5);
		layout.setTopHeight(5);
		
        last.set(tabPane.getComp());
        refresh();
	}

	/**
	 * 设置连接信息
	 */
	public void setValues(JdbcInfo jdbcInfo) {
		this.jdbcInfo = jdbcInfo;
		
		if(StringUtils.isBlank(jdbcInfo.getOtherDbType())) {
			typeInput.setDbTypeValue(DriverUtil.getDbType(jdbcInfo).name());	//优先设置数据库类型，防止做判断获取不到
		}else {
			typeInput.setDbTypeValue(jdbcInfo.getOtherDbType());
		}
        if (null != jdbcInfo.getCurrentVersion()) typeInput.setVersValue(jdbcInfo.getCurrentVersion().name());
		
		nameInput.setValue(jdbcInfo.getName());
		viewInput.setValue(jdbcInfo.getView().name());
		connectInput.setValue(jdbcInfo.getDbUrl());
		usernameInput.setValue(jdbcInfo.getUser());
		passwordInput.setValue(jdbcInfo.getPassword());
		passwordInput.setLocked(jdbcInfo.isLockPass());
		schemaInput.setValue(jdbcInfo.getSchema());
		userTypeInput.setValue(String.valueOf(jdbcInfo.isSys()));
		enabledInput.setValue(String.valueOf(jdbcInfo.isUseSshTunnel()));
		
		
		if(jdbcInfo.getSshBean()!=null) {
			sshPanel.loadFormData(jdbcInfo.getSshBean());
		}else {
			sshPanel.resetForm();
		}
		sshPanel.setEnabled(jdbcInfo.isUseSshTunnel());
		
		if(!StringUtils.isBlank(jdbcInfo.getScriptDirPath())) {
			chooseInput.setValue(jdbcInfo.getScriptDirPath());
		}else {
			chooseInput.setValue("");
		}
		
	}
	
	/**
	 * 获取连接信息
	 * @param removeQuote
	 * @return
	 */
	public JdbcInfo getLoginConnMsg() {
		JdbcInfo info = new JdbcInfo();
		if (null != jdbcInfo) info.setId(jdbcInfo.getId());
		info.setClazz(getDirverClass(typeInput.getDbTypeValue()));
		info.setName(nameInput.getValue());
		info.setView(JdbcInfo.View.valueOf(viewInput.getValue()));
		info.setDbUrl(connectInput.getValue());

		DBTypeEnum.Version ver = null;
        String str = typeInput.getVersValue();
        if (StringUtils.isNotBlank(str) && EnumUtil.contain(DBTypeEnum.Version.class, str)) {
            ver = DBTypeEnum.Version.valueOf(typeInput.getVersValue());
            info.setCurrentVersion(ver);
        }
        info.setUser(usernameInput.getValue());
		
		info.setLockPass(passwordInput.isLocked());
		info.setPassword(passwordInput.getValue());
		info.setSchema(schemaInput.getValue());
			

		info.setSys(Boolean.parseBoolean(userTypeInput.getValue()));
		info.setUseSshTunnel(enabledInput.isChecked());
		
		if(ExtDbTypeUtil.selfDefinedDbTypeMaps.containsKey(typeInput.getDbTypeValue())) {
			info.setOtherDbType(typeInput.getDbTypeValue());
		}
		
		
		SshBean sb = sshPanel.getFormData();
		
		if(enabledInput.isChecked()) {
			info.setSshBean(sshPanel.getFormData());
		}else {
			if(sb!=null&&!StringUtils.isBlank(sb.getIp())) {
				info.setSshBean(sshPanel.getFormData());
			}else {
				info.setSshBean(null);
			}
		}
			
		info.setScriptDirPath(chooseInput.getValue());
		//把用户输出的非标准大小写进行自动转换
		DBTypeEnum dbtype=DriverUtil.getDbType(info);
		if(!SqlStrUtil.isQuoted(info.getUser()))
			info.setUser(SqlStrUtil.rmQuote4Sel(info.getUser(), dbtype,info.getCurrentVersion()));
		if(!SqlStrUtil.isQuoted(info.getSchema()))
			info.setSchema(SqlStrUtil.rmQuote4Sel(info.getSchema(), dbtype,info.getCurrentVersion()));
		return info;
	}
	
	public void reset() {
		jdbcInfo = null;
		refreshTypeBox();
		nameInput.setValue("");
		autoSaveInput.setValue(String.valueOf(CsVarUtil.CONF.getAutoSaveConn()));
		viewInput.setValue(JdbcInfo.View.USER.name());
		typeInput.setDbTypeValue(DBTypeEnum.hhdb.name());
		connectInput.setValue(getDriverUrl());
		passwordInput.setValue("");
		passwordInput.setLocked(false);
		usernameInput.setValue("");
		schemaInput.setValue("");
//		chooseInput.setValue(DEFAULT+File.separator+typeInput.getValue());
		setSqlPath();
		sshPanel.resetForm();
        enabledInput.setValue(false);
        sshPanel.setEnabled(false);
	}
    /**
     * 初始化页面
     */
	private void refresh(){
		jdbcInfo = null;
        HeaderConfig generalHeaderConfig = newHeaderConfig(LangMgr.lang(Word2.GENERAL));
        generalHeaderConfig.setFontType(FaSolidEnum.PLUS);
        
        HeaderConfig sshHeaderConfig = newHeaderConfig("SSH");
        sshHeaderConfig.setFontType(FaSolidEnum.LOCK);
        
        LastPanel p = new LastPanel();
        p.setWithScroll(getSshPanel().getComp());
		tabPane.setPanel("ssh", p, sshHeaderConfig);
		tabPane.setPanel(TABID, getGeneralPanel(), generalHeaderConfig);
        tabPane.selectPanel("general");
		sshPanel.setEnabled(enabledInput.isChecked());
		
    }
	
	
	
	public boolean isBtnEnabled() {
		boolean enabled;
		String st = typeInput.getDbTypeValue();
		if (DBTypeEnum.redis.name().equals(st) || DBTypeEnum.sqlite.name().equals(st) || DBTypeEnum.mongodb.name().equals(st)
				|| DBTypeEnum.databricks.name().equals(st)) {
			enabled = StringUtils.isNotEmpty(nameInput.getValue());
		} else {
			enabled = StringUtils.isNotEmpty(nameInput.getValue())
					&& StringUtils.isNotEmpty(connectInput.getValue())
					&& StringUtils.isNotEmpty(usernameInput.getValue());
		}
		
		return enabled;
	}
	
	private LastPanel getGeneralPanel() {
		LabelInput userTypeLabe = new LabelInput(LangMgr.lang(Word4.USER_TYPE)+" : ", AlignEnum.LEFT);
		userTypeInput = new CheckBoxInput(USER_TYPE, LangMgr.sLang(CsL.SYS_DBA));
		LabelInput autoSaveLabe = new LabelInput("", AlignEnum.LEFT);
		autoSaveInput = new CheckBoxInput("autoSave",LangMgr.lang(Word4.AUTO_SAVE)) {
			@Override
			protected void onClick(ActionEvent e) {
				changeBtnStatus(isBtnEnabled());
			}
		};
		
		chooseInput = new DirChooserInput() {
			protected void onTextChange() {
				if(getComp().isShowing()) {
					changeBtnStatus(isBtnEnabled());
				}
			}
		} ;
		autoSaveInput.setValue(String.valueOf(CsVarUtil.CONF.getAutoSaveConn()));
		
		
		HPanel generalPanel = new HPanel(getLayout());
		nameInput = (TextInput)getInput(NAME);
		
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.NAME)+"* : ", AlignEnum.LEFT),nameInput);
		viewInput = new SelectBox("view");
		viewInput.addOption(LangMgr.lang(Word4.USER_VIEW), JdbcInfo.View.USER.name(), FaIconUtil.getIcon(FaSolidEnum.USER,new Color(105, 197, 255),16));
		viewInput.addOption(LangMgr.lang(Word4.DBA_VIEW), JdbcInfo.View.DBA.name(), FaIconUtil.getIcon(FaSolidEnum.USER,new Color(255, 119, 68),16));
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.VIEW)+"* : ", AlignEnum.LEFT),viewInput);
		typeInput = new DBTypeComboBox(){
			@Override
			protected void dbTypeItemChange(String value) {
				if (generalPanel.getComp().isShowing()) {
					generalPanel.remove(userTypeLabe, userTypeInput);
					
					if (EnumUtil.contain(DBTypeEnum.class, value) && DBTypeEnum.oracle == DBTypeEnum.valueOf(getDbTypeValue())) {
						generalPanel.remove(autoSaveLabe, autoSaveInput);
						generalPanel.add(userTypeLabe, userTypeInput, autoSaveLabe, autoSaveInput);
					}
					generalPanel.updateUI();
					connectInput.setValue(getDriverUrl());


					if (ExtDbTypeUtil.selfDefinedDbTypeMaps.containsKey(getDbTypeValue())) {
						viewInput.setEnabled(false);
						viewInput.setValue(JdbcInfo.View.USER.name());
					} else {
						viewInput.setEnabled(true);
						schemaInput.setEnabled(true);
						usernameInput.setEnabled(true);
						passwordInput.setLocked(false);
						
						if(!SqlWinUtil.showSchemaBox(DBTypeEnum.valueOf(getDbTypeValue())))
//						if ( DBTypeEnum.cache.name().equals(getDbTypeValue())
//								|| DBTypeEnum.derby.name().equals(getDbTypeValue()) || DBTypeEnum.sqlite.name().equals(getDbTypeValue())
//								|| DBTypeEnum.hana.name().equals(getDbTypeValue()) || DBTypeEnum.mongodb.name().equals(getDbTypeValue())) 
						{
//							viewInput.setEnabled(false);
							viewInput.setValue(JdbcInfo.View.USER.name());
							schemaInput.setEnabled(false);
							schemaInput.setValue("");
//							if (DBTypeEnum.sqlite.name().equals(getDbTypeValue())) {
//								usernameInput.setEnabled(false);
//								usernameInput.setValue("");
//								passwordInput.setLocked(true);
//								passwordInput.setValue("");
//							}
						}
					}
				}
				setSqlPath();
			}
			
			@Override
			protected void versItemChange(ItemEvent e) {
				if(null != connectInput && e.getStateChange()==ItemEvent.SELECTED) {
					connectInput.setValue(getDriverUrl());
				}
			}
		};
		refreshTypeBox();
		typeInput.setDbTypeValue(CsVarUtil.allSupportDbTypes.get(0).name());
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.TYPE)+"* : ", AlignEnum.LEFT),typeInput.getHpanel());
		HButton button = new HButton(){
			@Override
			protected void onClick() {
				connectInput.setValue(getDriverUrl());
			}
		};
		button.getComp().setCursor(Cursor.getDefaultCursor());
		button.setIcon(FaSolidEnum.OUTDENT,16);
		button.setToolTipText(LangMgr.lang(Word2.TEMPLATE));
		connectInput = new TextInputExt(getDriverUrl(), null, button, "") ;
		connectInput.getTextInput().getComp().getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				doChange(connectInput);
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				doChange(connectInput);
				
			}
			
			@Override
			
			
			public void changedUpdate(DocumentEvent e) {
				doChange(connectInput);
			}
		});
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.CONNECTIONS)+"* : ", AlignEnum.LEFT),connectInput);
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.USERNAME)+"* : ", AlignEnum.LEFT),usernameInput = (TextInput) getInput(USERNAME));
		passwordInput = new PasswordLockInput() {
			@Override
			protected void onChange() {
				changeBtnStatus(isBtnEnabled());
			}
		};
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.PASSWORD)+" : ", AlignEnum.LEFT),passwordInput.getHComp());
		generalPanel.add(new LabelInput(LangMgr.lang(Word2.MODE)+" : ", AlignEnum.LEFT),schemaInput = (TextInput) getInput(SCHEMA));
		generalPanel.add(new LabelInput(LangMgr.lang(Word4.SCRIPT_FROM)+" : ", AlignEnum.LEFT),chooseInput);
		
		
		
		generalPanel.add(autoSaveLabe,autoSaveInput);
		typeInput.setDbTypeValue(DBTypeEnum.hhdb.name());
		LastPanel lastPanel = new LastPanel();
		setSqlPath();
		lastPanel.set(generalPanel.getComp());
		
		return lastPanel;
	}

	
	private void refreshTypeBox() {
		typeInput.removeAllOption();
		List<String> supportNames=new ArrayList<>();
		for(DBTypeEnum dt:CsVarUtil.allSupportDbTypes) {
			supportNames.add(dt.name());
		}
		typeInput.setDataList(supportNames);
		PkgDbTypeUtil.addExtTypes(typeInput);
	}

	
	
	private void doChange(TextInputExt ext) {
			if(ext!=null&&ext.getComp().isShowing()) {
				changeBtnStatus(isBtnEnabled());
			}
				
	}
	
	
	
	private HDivLayout getLayout() {
		HDivLayout playout=new HDivLayout(GridSplitEnum.C3);
		playout.setTopHeight(20);
		playout.setLeftWidth(50);
		playout.setRightWidth(50);
		playout.setBottomHeight(20);
		playout.setyGap(20);
		return playout;
	}
	
	private HPanel getSshPanel() {
		HDivLayout playout=new HDivLayout(GridSplitEnum.C3);
		playout.setTopHeight(20);
		playout.setLeftWidth(0);
		playout.setRightWidth(50);
		playout.setBottomHeight(20);
		playout.setyGap(20);
		sshPanel = new SshPanel(playout){
			@Override
			protected void onContentChange() {
				if(this.comp!=null&&this.comp.isShowing()) {
					changeBtnStatus(isBtnEnabled());
				}
				
			}
		};
		enabledInput = new CheckBoxInput(ENABLED, LangMgr.sLang(CsL.ENABLED_SSH)) {
			@Override
			protected void onClick(ActionEvent e) {
				changeBtnStatus(isBtnEnabled());
				sshPanel.setEnabled(enabledInput.isChecked());
			}
		};
		enabledInput.setValue("false");
		sshPanel.setEnabled(false);
		HDivLayout layout = new HDivLayout();
		layout.setTopHeight(20);
		layout.setLeftWidth(50);
		HPanel headPanel = new HPanel(layout);
		headPanel.add(enabledInput);
		headPanel.add(sshPanel.getHPanel());
		return headPanel;
	}

	private String getDirverClass(String dbType) {
		if(ExtDbTypeUtil.selfDefinedDbTypeMaps.containsKey(dbType)) {
			return ExtDbTypeUtil.selfDefinedDbTypeMaps.get(dbType).getClazz();
		}else {
			return DriverUtil.getDriverClass(DBTypeEnum.valueOf(dbType));
		}
	}
	

	private String getDriverUrl() {
		String dbType=typeInput.getDbTypeValue();
		if(ExtDbTypeUtil.selfDefinedDbTypeMaps.containsKey(dbType)) {
			return ExtDbTypeUtil.selfDefinedDbTypeMaps.get(dbType).getUrl();
		}else {
			DBTypeEnum.Version ver=null;
			if(StringUtils.isNoneBlank(typeInput.getVersValue()))
				ver=DBTypeEnum.Version.valueOf(typeInput.getVersValue());
			return DriverUtil.getDriverUrl(DBTypeEnum.valueOf(dbType),ver);
		}
	}
	
	private void setSqlPath() {
		File f=new File(CsVarUtil.getSqlBtnDir(),typeInput.getDbTypeValue());
		chooseInput.setValue(f.getAbsolutePath());
	}
	
	
	private AbsInput getInput(String name) {
		AbsInput input;
		if (PASSWORD.equals(name)) {
			input = new PasswordInput(name) {
				@Override
				protected void doChange() {
					changeBtnStatus(isBtnEnabled());
				}
			};
		} else {
			input = new TextInput(name) {
				@Override
				protected void doChange() {
					changeBtnStatus(isBtnEnabled());
				}
			};
		}
		return input;
	}
	
	public HTabPanel getTabPane() {
		return tabPane;
	}
	
	public CheckBoxInput getAutoSaveInput() {
		return autoSaveInput;
	}
	

	protected abstract void changeBtnStatus(boolean enabled);
	
	private HeaderConfig newHeaderConfig(String name){
		HeaderConfig headerConfig = new HeaderConfig(name);
		headerConfig.setTitleEditable(false);
		headerConfig.setFixTab(true);
		headerConfig.setDetachEnabled(false);
		return headerConfig;
	}
}
