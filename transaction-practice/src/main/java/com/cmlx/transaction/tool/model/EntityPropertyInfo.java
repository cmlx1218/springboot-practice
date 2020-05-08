package com.cmlx.transaction.tool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.PropertyDescriptor;

/**
 * Entity对象属性信息类
 *
 * @author Gnoll
 * @create 2017-04-14 12:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityPropertyInfo {
    private PropertyDescriptor primaryKey;
    private PropertyDescriptor[] otherKey;
}
