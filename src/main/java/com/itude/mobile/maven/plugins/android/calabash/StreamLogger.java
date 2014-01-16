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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class StreamLogger extends Thread
{

  private final InputStream _stream;
  private final Process     _process;

  public StreamLogger(CalabashAndroidRunner cam, Process process, InputStream streamToPrint)
  {
    _stream = streamToPrint;
    _process = process;
  }

  @Override
  public void run()
  {
    try
    {
      InputStreamReader inputStreamReader = new InputStreamReader(_stream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        processLine(line);
      }
    }
    catch (IOException ioe)
    {
    }

  }

  public Process getProcess()
  {
    return _process;
  }

  /**
   * Implement this method to process lines that were returned from the provided stream 
   * @param line
   */
  public abstract void processLine(String line);
}
