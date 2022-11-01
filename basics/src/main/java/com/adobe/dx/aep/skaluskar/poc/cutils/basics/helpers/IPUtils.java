/**
 * IPUtils.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

import java.io.InputStream;
import java.net.InetAddress;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

/**
 * @author admin
 *
 */
public class IPUtils
{
  private static final String  IP_DATABASE_PATH =
      "/files/geolite2/GeoLite2-City.mmdb";
  private static final IPUtils INSTANCE         = new IPUtils();

  private DatabaseReader       dbReader;

  private IPUtils()
  {
    try
    {
      InputStream dbStream = this.getClass().getResourceAsStream(
          IP_DATABASE_PATH);
      dbReader = new DatabaseReader.Builder(dbStream).build();
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
  }

  public CityResponse cityResponse(String ipAddress)
  {
    CityResponse response = null;
    try
    {
      InetAddress inetAddress = InetAddress.getByName(ipAddress);
      response = dbReader.city(inetAddress);
    }
    catch (AddressNotFoundException e)
    {
      /* instead of throwing exception return a null */
      response = null;
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
    return response;
  }

  public static String getCity(String ipAddress)
  {
    CityResponse response = INSTANCE.cityResponse(ipAddress);
    return (response == null) ? StringUtils.STR_EMPTY
        : response.getCity().getName();
  }

  public static String getCountry(String ipAddress)
  {
    CityResponse response = INSTANCE.cityResponse(ipAddress);
    return (response == null) ? StringUtils.STR_EMPTY
        : response.getCountry().getName();
  }
}
