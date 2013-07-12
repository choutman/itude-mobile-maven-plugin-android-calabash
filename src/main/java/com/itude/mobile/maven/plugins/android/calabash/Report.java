package com.itude.mobile.maven.plugins.android.calabash;

import java.io.File;

public class Report
{
  /**
   * @parameter
   *    alias="format"
   */
  private String _format;

  /**
   * @parameter
   *    alias="path"
   */
  private File   _path;

  /**
   * @parameter
   *    alias="fileName"
   */
  private String _fileName;

  public String getFormat()
  {
    return _format;
  }

  public void setFormat(String format)
  {
    _format = format;
  }

  public File getPath()
  {
    return _path;
  }

  public void setPath(File path)
  {
    _path = path;
  }

  public String getFileName()
  {
    return _fileName;
  }

  public void setFileName(String fileName)
  {
    _fileName = fileName;
  }
}
