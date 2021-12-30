package com.cbs.springboot.bean;

public class ThreadLocalPool {
 public static ThreadLocal<CoupousBean> threadLocal = new ThreadLocal<CoupousBean>();
 public static ThreadLocal<String> targetThreadLocal = new ThreadLocal<String>();

}
