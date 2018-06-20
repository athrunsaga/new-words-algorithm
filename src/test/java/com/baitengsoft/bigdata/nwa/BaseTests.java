package com.baitengsoft.bigdata.nwa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BaseTests {

  /**
   * 从资源目录获取文本内容。
   * @param resFile 资源文件。
   * @return 返回文本内容。
   */
  protected String getPageFromResourceFile(String resFile) {
    if (!resFile.startsWith("/"))
      resFile = "/" + resFile;

    StringBuilder sb = new StringBuilder();
    try (BufferedReader resFileReader =
           new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resFile)))) {
      String line;
      while ((line = resFileReader.readLine()) != null)
        sb.append(line);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }
}
