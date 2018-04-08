package com.rograndec.jianeng.shiro;

import com.rograndec.jianeng.annotation.PasswordCheckType;
import com.rograndec.jianeng.service.ReflectService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * 自定义密码验证
 */
public class CloudCredentialsMatcher extends SimpleCredentialsMatcher{
  private Logger logger = LoggerFactory.getLogger(CloudCredentialsMatcher.class);

    /**
     * Todo: match userName
     * @param authcToken
     * @param referenceToken
     * @return
     */
  public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo referenceToken) {
      UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
      String userName = token.getUsername();
      char[] chrArray = token.getPassword();
      StringBuffer password = new StringBuffer();
      if (chrArray != null) {
          for (char chr : chrArray) {
              password.append(chr);
          }
      }
      Object accountCredentials = getCredentials(referenceToken);
      String dbUserName = referenceToken.toString();

      return true;
  }


  public Boolean getPasswordCheckResult(String inputPassword, String authenticationInfoPassword) {
      Class<?> passwordCheckTypeClass  = ReflectService.getPasswordCheckTypeClazz(PasswordCheckType.class);
      if (passwordCheckTypeClass == null) {
          return false;
      }
      try {
          // passwordAuth interface method
          Method checkMethod = passwordCheckTypeClass.getMethod(PasswordAuth.METHOD_NAME);
          if (checkMethod == null) {
              return false;
          }
          // 无参构造
          Constructor[] methods = passwordCheckTypeClass.getDeclaredConstructors();
          if (methods.length == 1 && ((Constructor)methods[0]).getParameterCount() == 0) {
            Boolean isMatched = (Boolean) checkMethod.invoke(passwordCheckTypeClass.newInstance());
            return isMatched;
          }
          // 有参构造方法
          Constructor constructor = passwordCheckTypeClass.getDeclaredConstructor();
          constructor.setAccessible(true);
          Object passwordCheckTypeClazzInstance = constructor.newInstance(inputPassword, authenticationInfoPassword);

          Boolean matched = (Boolean) checkMethod.invoke(passwordCheckTypeClazzInstance);
          return matched;
      } catch (Throwable e) {
          ;
      }
      return false;
  }
}
