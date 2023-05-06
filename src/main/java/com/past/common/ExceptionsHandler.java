package com.past.common;

import com.past.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 全局异常捕获拦截处理，返回自定义 统一响应返回实体ResponseResult  json数据
 */
@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

    /**
     * 处理 未认证异常
     * @param ex 未认证异常对象
     * @return 401.jsp
     */
    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseBody
    public ResponseResult unauthenticatedException(UnauthenticatedException ex) {

        log.error("当前执行登录操作的用户未认证异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户未认证异常！");
    }

    /**
     * 处理 未授权异常
     * @param ex 未授权异常对象
     * @return 401.jsp
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseResult unauthorizedException(UnauthorizedException ex) {

        log.error("当前执行登录操作的用户未授权异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户未授权异常！");
    }

    /**
     * 处理 用户凭证(password)信息过期异常
     * @param ex 用户凭证过期异常异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(ExpiredCredentialsException.class)
    @ResponseBody
    public ResponseResult expiredCredentialsExceptionHandler(ExpiredCredentialsException ex) {

        log.error("当前执行登录操作的用户凭证信息过期异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户凭证信息过期异常！");
    }

    /**
     * 处理 登录失败次数过多异常
     * @param ex 登录失败次数过多异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(ExcessiveAttemptsException.class)
    @ResponseBody
    public ResponseResult excessiveAttemptsExceptionHandler(ExcessiveAttemptsException ex) {

        log.error("登录失败次数过多异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("登录失败次数过多异常！");
    }

    /**
     * 处理 用户主身份信息被锁定异常，即登录的用户主身份信息(username或phone或email)被锁定
     * @param ex 用户主身份信息被锁定异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(LockedAccountException.class)
    @ResponseBody
    public ResponseResult lockedAccountExceptionHandler(LockedAccountException ex) {

        log.error("当前执行登录操作的用户主身份信息被锁定异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户主身份信息被锁定异常！");
    }

    /**
     * 处理 用户主身份信息被禁用异常，即登录的用户主身份信息(username或phone或email)被禁用
     * @param ex 用户主身份信息被禁用异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(DisabledAccountException.class)
    @ResponseBody
    public ResponseResult disabledAccountExceptionHandler(DisabledAccountException ex) {

        log.error("当前执行登录操作的用户主身份信息被禁用异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户主身份信息被禁用异常！");
    }

    /**
     * 处理 用户凭证信息错误异常，即登录的用户凭证信息(password)错误或不存在
     * @param ex 用户凭证信息错误异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(IncorrectCredentialsException.class)
    @ResponseBody
    public ResponseResult incorrectCredentialsExceptionHandler(IncorrectCredentialsException ex) {

        log.error("当前执行登录操作的用户凭证信息错误异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户凭证信息错误异常！");
    }

    /**
     * 处理 用户主身份信息错误异常，即登录的用户主身份信息(username或phone或email)错误或不存在
     * @param ex 用户主身份信息错误异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(UnknownAccountException.class)
    @ResponseBody
    public ResponseResult unknownAccountExceptionHandler(UnknownAccountException ex) {

        log.error("当前执行登录操作的用户主身份信息错误异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("当前执行登录操作的用户主身份信息错误异常！");
    }

    /**
     * 处理 自定义参数异常
     * @param ex 自定义参数异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(ParamException.class)
    @ResponseBody
    public ResponseResult paramExceptionHandler(ParamException ex) {

        log.error("参数异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("参数异常！");
    }

    /**
     * 处理 运行时异常
     * @param ex 运行时异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseResult runtimeExceptionHandler(RuntimeException ex) {

        log.error("运行时异常：{}", ex.getMessage(), ex);
        return ResponseResult.error("运行时异常！");
    }

    /**
     * 处理 空指针异常
     * @param ex 空指针异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseResult nullPointerExceptionHandler(NullPointerException ex) {

        log.error("空指针异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("空指针异常！");
    }

    /**
     * 处理 类型转换异常
     * @param ex 类型转换异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public ResponseResult classCastExceptionHandler(ClassCastException ex) {

        log.error("类型转换异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("类型转换异常！");
    }

    /**
     * 处理 文件未找到异常
     * @param ex 文件未找到异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    public ResponseResult fileNotFoundException(FileNotFoundException ex) {

        log.error("文件未找到异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("文件未找到异常！");
    }

    /**
     * 处理 数字格式异常
     * @param ex 数字格式异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseBody
    public ResponseResult numberFormatException(NumberFormatException ex) {

        log.error("数字格式异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("数字格式异常！");
    }

    /**
     * 处理 安全异常
     * @param ex 安全异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseBody
    public ResponseResult securityException(SecurityException ex) {

        log.error("安全异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("安全异常！");
    }

    /**
     * 处理 sql异常
     * @param ex sql异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseResult sqlException(SQLException ex) {

        log.error("sql异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("sql异常！");
    }

    /**
     * 处理 类型不存在异常
     * @param ex 类型不存在异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(TypeNotPresentException.class)
    @ResponseBody
    public ResponseResult typeNotPresentException(TypeNotPresentException ex) {

        log.error("类型不存在异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("类型不存在异常！");
    }

    /**
     * 处理 IO异常
     * @param ex IO异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseResult ioExceptionHandler(IOException ex) {

        log.error("IO异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("IO异常！");
    }

    /**
     * 处理 未知方法异常
     * @param ex 未知方法异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(NoSuchMethodException.class)
    @ResponseBody
    public ResponseResult noSuchMethodExceptionHandler(NoSuchMethodException ex) {

        log.error("未知方法异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("未知方法异常！");
    }

    /**
     * 处理 数组越界异常
     * @param ex 数组越界异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public ResponseResult indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {

        log.error("数组越界异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("数组越界异常！");
    }

    /**
     * 处理 sql语法错误异常
     * @param ex sql语法错误异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public ResponseResult badSqlGrammarException(BadSqlGrammarException ex) {

        log.error("sql语法错误异常：{} ", ex.getMessage(), ex);
        return ResponseResult.error("sql语法错误异常！");
    }

    /**
     * 处理 无法注入bean异常
     * @param ex 无法注入bean异常对象
     * @return ResponseResult
     */
    @ExceptionHandler(NoSuchBeanDefinitionException.class)
    @ResponseBody
    public ResponseResult noSuchBeanDefinitionException(NoSuchBeanDefinitionException ex) {

        log.error("无法注入bean异常 ：{} ", ex.getMessage(), ex);
        return ResponseResult.error("无法注入bean！");
    }

    /**
     * 处理 Http消息不可读异常
     * @param ex Http消息不可读异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseResult requestNotReadable(HttpMessageNotReadableException ex) {

        log.error("400错误..requestNotReadable：{} ", ex.getMessage(), ex);
        return ResponseResult.error("Http消息不可读！");
    }

    /**
     * 处理 400错误
     * @param ex 引发400错误的异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public ResponseResult requestTypeMismatch(TypeMismatchException ex) {

        log.error("400错误..TypeMismatchException：{} ", ex.getMessage(), ex);
        return ResponseResult.error("服务器异常！");
    }

    /**
     * 处理 500错误
     * @param ex 引发500错误的异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    @ResponseBody
    public ResponseResult server500(RuntimeException ex) {

        log.error("500错误：{} ", ex.getMessage(), ex);
        return ResponseResult.error("服务器异常！");
    }

    /**
     * 处理 栈溢出异常
     * @param ex 栈溢出异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({StackOverflowError.class})
    @ResponseBody
    public ResponseResult requestStackOverflow(StackOverflowError ex) {

        log.error("栈溢出：{} ", ex.getMessage(), ex);
        return ResponseResult.error("栈溢出异常！");
    }

    /**
     * 处理 除数不能为0异常
     * @param ex 除数不能为0异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({ArithmeticException.class})
    @ResponseBody
    public ResponseResult arithmeticException(ArithmeticException ex) {

        log.error("除数不能为0：{} ", ex.getMessage(), ex);
        return ResponseResult.error("除数不能为0异常！");
    }

    /**
     * 处理 Exception异常
     * @param ex Exception异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseResult exception(Exception ex) {

        log.error("其他错误：{} ", ex.getMessage(), ex);
        return ResponseResult.error("Exception其他错误，请重试！");
    }

    /**
     * 处理 Throwable异常
     * 如果上述异常都处理不了，由它来兜底
     * @param ex Throwable异常对象
     * @return ResponseResult
     */
    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public ResponseResult throwable(Throwable ex) {

        log.error("系统错误：{} ", ex.getMessage(), ex);
        return ResponseResult.error("Throwable系统错误，请重试！");
    }

}
