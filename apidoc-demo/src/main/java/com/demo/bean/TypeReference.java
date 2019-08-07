package com.demo.bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型引用
 * 用以获取泛型的类型
 * @param <T> 泛型
 */
public abstract class TypeReference<T> {

  private final Type type;

  public TypeReference() {
    Type superClass = getClass().getGenericSuperclass();
    if (superClass instanceof Class) {
      throw new RuntimeException("不支持的类型或者检查参数是否已经添加{},eg: Mock.mock(new TypeReference<Integer>(){})");
    }
    type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }

  public Type getType() {
    return type;
  }

}
