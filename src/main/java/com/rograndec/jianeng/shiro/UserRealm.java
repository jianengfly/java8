package com.rograndec.jianeng.shiro;

import com.rograndec.jianeng.annotation.PasswordCheckType;
import com.rograndec.jianeng.annotation.enumtype.ShiroCheckType;
import com.rograndec.jianeng.annotation.login.FindUserMethod;
import com.rograndec.jianeng.annotation.login.LoginService;
import com.rograndec.jianeng.annotation.login.Password;
import com.rograndec.jianeng.annotation.login.PermissionRoleMethod;
import com.rograndec.jianeng.config.SpringContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.lang.reflect.*;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthorizingRealm;

import com.rograndec.jianeng.service.ReflectService;
import org.springframework.stereotype.Service;

public class UserRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);

    /**
     * 获取用户权限和角色
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String loginName = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // reflect find service method
        if (!StringUtils.isNotEmpty(loginName)) {
            return authorizationInfo;
        }

        Map<String, List<String>> permAndRoleMap = getPermissionRole(loginName);
        if (permAndRoleMap == null || permAndRoleMap.isEmpty()) {
            return authorizationInfo;
        }
        if (permAndRoleMap.containsKey(ShiroCheckType.perms.toString())) {
           authorizationInfo.addStringPermissions(permAndRoleMap.get(ShiroCheckType.perms.toString()));
        }
        if (permAndRoleMap.containsKey(ShiroCheckType.roles.toString())) {
            authorizationInfo.addRoles(permAndRoleMap.get(ShiroCheckType.roles.toString()));
        }
        return authorizationInfo;
    }

    /**
     * 登录验证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String loginName = (String) token.getPrincipal();
        if (StringUtils.isEmpty(loginName)) {
            return null;
        }
        // check user
        Object userObj = getUser(loginName);
        if (userObj == null) {
            return null;
        }
        // get password check type clazz
        Class<?> clazz = ReflectService.getPasswordCheckTypeClazz(PasswordCheckType.class);
        if (clazz == null) {
            return null;
        }
        String password = "";
        if (clazz.getAnnotation(PasswordCheckType.class).database()) {
            // find user pwd from db
            String dbpassword = getPasswordByUser(userObj);
            if (StringUtils.isNotEmpty(dbpassword)) {
                password = dbpassword;
            }
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(loginName, password, getName());
        return authenticationInfo;
    }

    public void initCredentialsMatcher () {
        CloudCredentialsMatcher matcher = new CloudCredentialsMatcher();
        setCredentialsMatcher(matcher);
    }

    /**
     * 获取用户
     * @param loginName
     * @return
     */
    public static Object getUser (String loginName) {
        try {
            Method method = ReflectService.getServiceMethod(LoginService.class, FindUserMethod.class);
            if (method == null) {
                return null;
            }
            String serviceBeanName = method.getDeclaringClass().getAnnotation(Service.class).value();
            if (serviceBeanName.isEmpty()) {
                serviceBeanName = toLowerCaseFirstStr(method.getDeclaringClass().getSimpleName());
            }
            Object serviceObj = SpringContext.getBean(serviceBeanName);
            Object obj = method.invoke(serviceObj, loginName);
            return obj;
        } catch (Throwable e) {
            ;
        }
        return null;
    }

    /**
     * 获取用户密码
     *
     * @param obj
     * @return
     */
    public static String getPasswordByUser (Object obj) {
        try {
            Class userClass = (Class) obj.getClass();
            Field passwordField = null;
            Field[] fields = userClass.getDeclaredFields();
            for (Field f : fields) {
                if (!f.isAnnotationPresent(Password.class)) {
                    continue;
                }
                passwordField = f;
            }
            if (passwordField == null) {
                return null;
            }
            String passwordFieldName = passwordField.getAnnotation(Password.class).values();
            if (passwordFieldName == null) {
                passwordFieldName = passwordField.getName();
            }
            passwordField.setAccessible(true);
            return (String) passwordField.get(passwordFieldName);
        } catch (Throwable e) {
            ;
        }
        return null;
    }

    public Map<String, List<String>> getPermissionRole(String loginName) {
        try {
            Method findPermRoleMethod = ReflectService.getServiceMethod(LoginService.class, PermissionRoleMethod.class);
            if (findPermRoleMethod == null) {
                return null;
            }
            String serviceBeanName = findPermRoleMethod.getDeclaringClass().getAnnotation(Service.class).value();
            if (serviceBeanName.isEmpty()) {
                serviceBeanName = toLowerCaseFirstStr(findPermRoleMethod.getDeclaringClass().getSimpleName());
            }
            Object serviceObj = SpringContext.getBean(serviceBeanName);
            Object permsAndRoleMapObj = findPermRoleMethod.invoke(serviceObj, loginName);
            return (Map<String, List<String>>) permsAndRoleMapObj;
        } catch (Throwable e) {
            ;
        }
        return null;
    }


    /**
     * 首字母大写
     *
     * @param serviceName
     * @return
     */
    public static String toLowerCaseFirstStr(String serviceName) {
        if (serviceName.isEmpty()) {
            return null;
        }
        String first = serviceName.substring(0, 1);
        String residue = serviceName.substring(1);
        return first.toUpperCase() + residue;
    }
}