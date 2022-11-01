package com.adobe.dx.aep.poc.cutils.basics.test.expressions;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import com.adobe.dx.aep.poc.cutils.basics.serde.SerDeUtils;

import lombok.Getter;
import lombok.Setter;

public class SerdeTest
{
  private static final double DELTA = 10 ^ -10;

  private static final String EX1   =
      "{\"f1\": 1, \"f2\": 2, \"f3\": 3}";

  private static final String EX2   =
      "{\"f1\": 1.0, \"f2\": 2.0, \"f3\": 3.0}";

  private static final String EX3   =
      "{\"f1\": \"v1\", \"f2\": \"v2\", \"f3\": \"v3\"}";

  private static final String EX4   =
      "{\"m1\": {\"f1\": 1, \"f2\": \"v1\"}, "
          + "\"m2\": {\"f1\": 2, \"f2\": \"v2\"}}";

  @Test
  public void testDeserMap1()
  {
    Map<String, Object> map = SerDeUtils.deserializeMapFromJson(EX1,
        Integer.class);
    Assert.assertNotNull(map);
    Assert.assertEquals(1, ((Integer) map.get("f1")).intValue());
    Assert.assertEquals(2, ((Integer) map.get("f2")).intValue());
    Assert.assertEquals(3, ((Integer) map.get("f3")).intValue());
  }

  @Test
  public void testDeserMap2()
  {
    Map<String, Object> map = SerDeUtils.deserializeMapFromJson(EX2,
        Double.class);
    Assert.assertNotNull(map);
    Assert.assertEquals(1.0, ((Double) map.get("f1")).doubleValue(), DELTA);
    Assert.assertEquals(2.0, ((Double) map.get("f2")).doubleValue(), DELTA);
    Assert.assertEquals(3.0, ((Double) map.get("f3")).doubleValue(), DELTA);
  }

  @Test
  public void testDeserMap3()
  {
    Map<String, Object> map = SerDeUtils.deserializeMapFromJson(EX3,
        String.class);
    Assert.assertNotNull(map);
    Assert.assertEquals("v1", (String) map.get("f1"));
    Assert.assertEquals("v2", (String) map.get("f2"));
    Assert.assertEquals("v3", (String) map.get("f3"));
  }

  @Test
  public void testDeserMap4()
  {
    Map<String, Object> map = SerDeUtils.deserializeMapFromJson(EX4,
        TestClass.class);
    Assert.assertNotNull(map);

    TestClass m1 = (TestClass) map.get("m1");
    Assert.assertNotNull(m1);
    Assert.assertEquals(1, m1.f1);
    Assert.assertEquals("v1", m1.f2);

    TestClass m2 = (TestClass) map.get("m2");
    Assert.assertNotNull(m2);
    Assert.assertEquals(2, m2.f1);
    Assert.assertEquals("v2", m2.f2);
  }

  @Getter
  @Setter
  private static class TestClass
  {
    private int    f1;

    private String f2;
  }
}
