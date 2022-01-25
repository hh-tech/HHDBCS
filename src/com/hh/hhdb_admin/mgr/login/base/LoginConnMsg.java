package com.hh.hhdb_admin.mgr.login.base;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.connMgr.base.AbsConnMsg;
import com.hh.frame.swingui.view.connMgr.base.ConnMgrUtil;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;

public class LoginConnMsg extends AbsConnMsg {

    private String view = "";
    private String connect = "";
    private boolean encrypted = false;
    private String schema = "";
    private boolean userType = false;
    private boolean enabled = false;
    private String host = "";
    private String port = "";
    private String sshUsername = "";
    private String sshType = "PASSWORD";
    private boolean sshEncrypted = false;
    private String sshPassword = "";
    private String privateKey = "";
    private String privatePassword = "";

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isUserType() {
        return userType;
    }

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSshUsername() {
        return sshUsername;
    }

    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    public String getSshType() {
        return sshType;
    }

    public void setSshType(String sshType) {
        this.sshType = sshType;
    }

    public boolean isSshEncrypted() {
        return sshEncrypted;
    }

    public void setSshEncrypted(boolean sshEncrypted) {
        this.sshEncrypted = sshEncrypted;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public String getDecryptSshPassword() {
        return ConnMgrUtil.decryptData(sshPassword);
    }

    public void encryptedSshPassword() {
        sshPassword = ConnMgrUtil.encryptData(sshPassword);
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivatePassword() {
        return privatePassword;
    }

    public String getDecryptPrivatePassword() {
        return ConnMgrUtil.decryptData(privatePassword);
    }

    public void encryptedPrivatePassword() {
        privatePassword = ConnMgrUtil.encryptData(privatePassword);
    }

    public void setPrivatePassword(String privatePassword) {
        this.privatePassword = privatePassword;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add(CommonComp.NAME, connName);
        json.add(CommonComp.VIEW, view);
        json.add(CommonComp.TYPE, type);
        json.add(CommonComp.CONNECT, connect);
        json.add(CommonComp.USERNAME, username);
        json.add(CommonComp.ENCRYPTED, encrypted);
        json.add(CommonComp.PASSWORD, password);
        json.add(CommonComp.SCHEMA, schema);
        json.add(CommonComp.USER_TYPE, userType);
        json.add(CommonComp.ENABLED, enabled);
        JsonObject sshJson = new JsonObject();
        sshJson.add(CommonComp.HOST, host);
        sshJson.add(CommonComp.PORT, port);
        sshJson.add(CommonComp.SSH_USERNAME, sshUsername);
        sshJson.add(CommonComp.SSH_TYPE, sshType);
        sshJson.add(CommonComp.SSH_ENCRYPTED, sshEncrypted);
        sshJson.add(CommonComp.SSH_PASSWORD, sshPassword);
        sshJson.add(CommonComp.PRIVATE_KEY, privateKey);
        sshJson.add(CommonComp.PRIVATE_PASSWORD, privatePassword);
        json.add("ssh", sshJson);
        return json;
    }

    @Override
    public void setJson(JsonObject json) {
        super.connName = json.getString(CommonComp.NAME);
        this.view = json.getString(CommonComp.VIEW);
        super.type = json.getString(CommonComp.TYPE);
        this.connect = json.getString(CommonComp.CONNECT);
        super.username = json.getString(CommonComp.USERNAME);
        super.password = json.getString(CommonComp.PASSWORD);
        this.encrypted = json.getBoolean(CommonComp.ENCRYPTED);
        this.schema = json.getString(CommonComp.SCHEMA);
        this.userType = json.getBoolean(CommonComp.USER_TYPE);
        this.enabled = json.getBoolean(CommonComp.ENABLED);
        JsonObject sshJson = json.get("ssh").asObject();
        this.host = sshJson.getString(CommonComp.HOST);
        this.port = sshJson.getString(CommonComp.PORT);
        this.sshUsername = sshJson.getString(CommonComp.SSH_USERNAME);
        this.sshType = sshJson.getString(CommonComp.SSH_TYPE);
        this.sshEncrypted = sshJson.getBoolean(CommonComp.SSH_ENCRYPTED);
        this.sshPassword = sshJson.getString(CommonComp.SSH_PASSWORD);
        this.privateKey = sshJson.getString(CommonComp.PRIVATE_KEY);
        this.privatePassword = sshJson.getString(CommonComp.PRIVATE_PASSWORD);
    }

    @Override
    public String toString() {
        return "LoginConnMsg{" +
                "view='" + view + '\'' +
                ", connect='" + connect + '\'' +
                ", encrypted=" + encrypted +
                ", schema='" + schema + '\'' +
                ", userType=" + userType +
                ", enabled=" + enabled +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", sshUsername='" + sshUsername + '\'' +
                ", sshType='" + sshType + '\'' +
                ", sshEncrypted=" + sshEncrypted +
                ", sshPassword='" + sshPassword + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", privatePassword='" + privatePassword + '\'' +
                '}';
    }
}
