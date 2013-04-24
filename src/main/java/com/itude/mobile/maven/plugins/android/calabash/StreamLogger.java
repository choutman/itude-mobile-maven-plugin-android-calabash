package com.itude.mobile.maven.plugins.android.calabash;

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
