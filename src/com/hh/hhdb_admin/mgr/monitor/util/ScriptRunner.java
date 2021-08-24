package com.hh.hhdb_admin.mgr.monitor.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    private final SSHClient sshClient;
    private final String host;
    private final String user;
    private final String password;
    private final boolean isPasswordAuth;
    private String outMsg = "";

    /**
     * 使用用户名和密码的登陆方式
     *
     * @param host 主机
     * @param user 用户名
     * @param password 密码
     */
    public ScriptRunner(String host, String user, String password) {
        sshClient = new SSHClient();
        this.host = host;
        this.user = user;
        this.password = password;
        this.isPasswordAuth = true;
    }

    public void run(String script, int timeout) throws IOException {
        if (!sshClient.isConnected()) {
            return;
        }
        try (Session session = sshClient.startSession(); Command cmd = session.exec(script + "\n")) {
            if (timeout > 0) {
                cmd.join(timeout, TimeUnit.SECONDS);
            }
            ByteArrayOutputStream output = IOUtils.readFully(cmd.getInputStream());
            Charset charset = session.getRemoteCharset();
            outMsg = new String(output.toByteArray(), charset);
            IOUtils.closeQuietly(output);
            output = IOUtils.readFully(cmd.getErrorStream());
            IOUtils.closeQuietly(output);
        }
    }

    public void connect() throws IOException {
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            sshClient.connect(host, 22);
            if (isPasswordAuth) {
                sshClient.authPassword(user, password);
            } else {
                sshClient.authPublickey(user);
            }
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public String getOutMsg() {
        return outMsg;
    }

    public void close() {
        try {
            sshClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}