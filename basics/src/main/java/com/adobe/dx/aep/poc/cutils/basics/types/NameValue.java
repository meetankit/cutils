/**
 * NameValue.java
 */
package com.adobe.dx.aep.poc.cutils.basics.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 *
 *         Basic name value pair that supports XML serialization, and can be
 *         used as a data element.
 */
@XmlType(name = "nameValue")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameValue
{
  private String name;

  private String value;
}
