package com.itude.mobile.maven.plugins.android.calabash;

/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
