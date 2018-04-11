package com.rograndec.jianeng.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernamePasswordProxyToken extends UsernamePasswordToken {

    public boolean isProxyLogin;

    public boolean isProxyLogin() {
        return isProxyLogin;
    }

    public void setProxyLogin(boolean isProxyLogin) {
        this.isProxyLogin = isProxyLogin;
    }

    public static UsernamePasswordToken asProxyLogin (UsernamePasswordToken upt, boolean isProxyLogin) {
        UsernamePasswordProxyToken cup = new UsernamePasswordProxyToken();
        if (upt != null) {
            cup.setHost(upt.getHost());
            char[] password = upt.getPassword();

            char[] newPassword = null;

            if (password != null) {
                newPassword = new char[password.length];
                for (int i = 0; i < password.length; ++i) {
                    newPassword[i] = password[i];
                }
            }
            cup.setPassword(newPassword);
            cup.setUsername(upt.getUsername());
        }
        cup.setProxyLogin(isProxyLogin);
        return cup;
    }
}
