package com.itude.mobile.maven.plugins.android.calabash;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Wrapper plugin to run calabash for Android. Plugin depends on the calabash-android commandline to function.
 * 
 * @goal test
 * @description  Wrapper plugin to run calabash for Android. Plugin depends on the calabash-android commandline to function.
 */
public class CalabashAndroidRunner extends AbstractMojo
{
  public static String PARAMETER_TAG     = "--tag";
  public static String PARAMETER_TAGS    = "--tags";
  public static String PARAMETER_FORMAT  = "--format";
  public static String PARAMETER_OUTPUT  = "--out";
  public static String PARAMETER_VERBOSE = "--verbose";

  /**
   * @parameter 
   *    alias="tags"
   */
  private String[]     _tags;

  /**
   * @parameter 
   *    alias="features"
   */
  private String[]     _features;

  /**
   * @parameter 
   *    alias="command"
   *    default-value="calabash-android" 
   */
  private String       _command;

  /**
   * @parameter 
   *    alias="action"
   *    default-value="run"
   */
  private String       _action;

  /**
   * @parameter 
   *    alias="apkRootFolder"
   */
  private String       _apkRootFolder;

  /**
   * @parameter 
   *    alias="apkNameRegex"
   *    default-value=".*[^aligned]\\.apk$"
   */
  private String       _apkNameRegex;

  /**
   * @parameter
   *    alias="verbose"
   *    default-value="false"
   */
  private boolean      _verbose;

  /**
   * @parameter 
   *    alias="format"
   */
  private String       _format;

  /**
   * @parameter 
   *    alias="outputFile"
   */
  private String       _outputFile;

  /**
   * @parameter 
   *    alias = "outputFolder"
   *    expression = "${project.build.directory}/calabash"
   */
  private File         _outputFolder;

  /**
   * @parameter 
   *    alias = "featuresFolder"
   *    expression = "${project.build.testOutputDirectory}"
   */
  private String       _featuresFolder;

  /**
   * @parameter 
   *    alias="customParameters"
   */
  private String[]     _customParameters;

  /**
   * We want to execute our command from the current project directory
   * @parameter 
   *    alias = "projectDirectory"
   *    expression="${project.basedir}"
   * @required
   */
  private File         _projectDirectory;

  @Override
  public void execute() throws MojoExecutionException
  {
    runCalabashProcess();
  }

  /**
   * The main method that will initiate the calabash process and handle the feedback the process gets
   */
  private void runCalabashProcess()
  {

    try
    {
      ProcessBuilder pb = getCalabashProcessBuilder();
      pb.directory(_outputFolder);

      getLog().info("Running command: '" + getPrintableCommand(pb.command()) + "'");

      Process process = pb.start();

      /*
       * Read our errors and print them to our error logging.
       * In case of some expected errors we want to abort/destroy our calabash process
       */
      StreamLogger errorLogger = new StreamLogger(this, process, process.getErrorStream())
      {

        @Override
        public void processLine(String line)
        {
          if (line.contains("error in opening zip file"))
          {
            getProcess().destroy();

            getLog().error("No usable apk was found in path \"" + getCanonicalApkRootFolder().toString()
                               + "\". Aborting operation Calabash.");
          }
          else if (line.contains("device not found"))
          {
            /*
             * If we are waiting for a device it means that it's either not attached
             * or not available for testing. We will abort otherwise the script will wait for a device indeterminately.
             */
            getProcess().destroy();
            getLog().error("No device was found to run our calabash tests on. Aborting operation Calabash.");
          }
          else
          {
            getLog().error(line);
          }

        }
      };
      errorLogger.start();

      /*
       * We want to log our default inpustream to the info log
       */
      StreamLogger inputLogger = new StreamLogger(this, process, process.getInputStream())
      {

        @Override
        public void processLine(String line)
        {
          getLog().info(line);
        }
      };
      inputLogger.start();

      /*
       * Wait to get the exit value. If 0 returns everything went as expected. 
       */
      try
      {
        int exitValue = process.waitFor();

        if (exitValue > 0)
        {
          getLog().error("The Calabash test for Android failed!");
          //          throw new RuntimeException("The Calabash test for Android failed!");
        }
      }
      catch (InterruptedException e)
      {
      }

    }
    catch (IOException e)
    {
    }

  }

  /**
   * The process that we will initiate will be based on provided parameters. 
   * We collect provided parameters and build up our process 
   * @return ProcessBuilder that contains all provided parameters
   */
  private ProcessBuilder getCalabashProcessBuilder()
  {
    ArrayList<String> commands = new ArrayList<String>();
    commands.add(_command);
    commands.add(_action);

    /*
     * Add our apk path to the commands or throw an expception if no apk file was found
     */
    File apkFile = getApkFile();
    if (apkFile != null)
    {
      commands.add(getApkFile().toString());
    }
    else
    {
      throw new RuntimeException("No apk file was found in the specified path: " + getCanonicalApkRootFolder().toString());
    }

    /*
     * Add features to the commands if we have any 
     */
    processFeatures(commands);

    /*
     * Let's process our tags if we have any
     */
    if (_tags != null && _tags.length > 0)
    {
      processTags(commands);
    }

    /*
     * How do we want to format our result
     */
    if (_format != null && _format.length() > 0)
    {
      commands.add(PARAMETER_FORMAT);
      commands.add(_format);
    }

    /*
     * Where do we want our output to be saved
     */
    processOutputFile(commands);

    /*
     * Do we want to run our calabash verbosely?
     */
    if (_verbose)
    {
      commands.add(PARAMETER_VERBOSE);
    }

    /*
     * In case we want to have some custom parameters
     */
    if (_customParameters != null && _customParameters.length > 0)
    {
      processCustomParameters(commands);
    }

    return new ProcessBuilder(commands);
  }

  /**
   * Returns the apk file which will be used in the calabash script
   * @return Single APK file that represents the file that was found using the apkRootFolder and the apkNameRegex 
   * or null if no apk file was found
   */
  private File getApkFile()
  {
    File canonicalFile = getCanonicalApkRootFolder();

    if (canonicalFile != null && canonicalFile.isDirectory())
    {
      /*
       * We got ourselves an existing folder. Let's check if it contains a valid apk
       */
      if (_apkNameRegex != null && _apkNameRegex.length() > 0)
      {
        final Pattern regex = Pattern.compile(_apkNameRegex);

        File[] apkFiles = canonicalFile.listFiles(new FilenameFilter()
        {

          @Override
          public boolean accept(File dir, String name)
          {
            return regex.matcher(name).matches();
          }
        });

        /*
         * If we found a file that matches our regular expression we will only return the first
         */
        if (apkFiles.length > 0)
        {
          return apkFiles[0];
        }
      }

    }

    return null;
  }

  /**
   * Returns the canonical root folder. This means we could use '../'-like formatting when providing the folder name
   * @return Canonical root folder if rootfolder exists or null if it doesn't
   */
  private File getCanonicalApkRootFolder()
  {
    if (_apkRootFolder != null && _apkRootFolder.length() > 0)
    {
      /*
       * We want to base our specified path on the projectDirectory
       */
      File path = new File(_projectDirectory, _apkRootFolder);
      try
      {
        return path.getCanonicalFile();
      }
      catch (IOException e)
      {
        return null;
      }

    }

    return null;
  }

  /**
   * If features were provided and they actually exist on the filesystem we want to add them to our commands 
   * that will be used to build up our process
   * @param commands
   */
  private void processFeatures(ArrayList<String> commands)
  {
    // by default, at least add the default path
    commands.add(_featuresFolder);

    if (_features != null && _features.length > 0)
    {
      /*
       * Loop through all provided features
       */
      for (String feature : _features)
      {
        File featureFile = new File(feature);
        try
        {
          /*
           * We only want to add our feature to the command if it actually exists.
           * If not then we want to inform the user about this.
           */
          if (featureFile.isFile() || featureFile.isDirectory())
          {
            commands.add(featureFile.getCanonicalPath());
          }
          else
          {
            getLog()
                .warn("Feature file '"
                          + featureFile.getCanonicalPath()
                          + "' does not exist. \nCheck your plugin configuration and the existence of the specified feature file and run the script again.");
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * If one or more tags were provided we want to add these to the commands. 
   * Also we want to add the necessary '@' before the tag and a ',' after the tag if multiple tags were provided
   * @param commands
   */
  private void processTags(ArrayList<String> commands)
  {
    if (_tags.length == 1)
    {
      /*
       * Only one tag was provided
       */
      commands.add(PARAMETER_TAG);
      commands.add("@" + _tags[0]);
    }
    else
    {
      /*
       * Multiple tags were provided
       */
      commands.add(PARAMETER_TAGS);

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < _tags.length; i++)
      {
        sb.append("@").append(_tags[i]);

        if (i < _tags.length - 1)
        {
          sb.append(",");
        }
      }
      commands.add(sb.toString());
    }
  }

  /**
   * Output files can be used to write json to for example. 
   * If we provided an outputFolder we want to make sure it exists before trying to make calabash write to a non-existing folder.
   * @param commands
   */
  private void processOutputFile(ArrayList<String> commands)
  {
    if (_outputFolder == null)
    {
      getLog().debug("No output folder specified");
      return;
    }
    File output;

    try
    {
      if (!_outputFolder.isDirectory())
      {
        getLog().info("Creating non-existing output  folder: '" + _outputFolder.getCanonicalPath() + "'");
        _outputFolder.mkdirs();
      }

      if ("junit".equals(_format))
      {
        output = _outputFolder;
      }
      else
      {
        output = new File(_outputFolder, _outputFile);
      }

      commands.add(PARAMETER_OUTPUT);
      commands.add(output.getCanonicalPath());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  /**
   * User can provide custom parameters. This method will add these parameters as the commands used to start the process.
   * @param commands
   */
  private void processCustomParameters(ArrayList<String> commands)
  {
    for (String parameter : _customParameters)
    {
      commands.add(parameter);
    }

  }

  /**
   * Convenience method to allow us to neatly print the process that will be run
   * @param commands
   * @return
   */
  private String getPrintableCommand(List<String> commands)
  {
    StringBuilder printableCommandBuilder = new StringBuilder();

    for (int i = 0; i < commands.size(); i++)
    {
      printableCommandBuilder.append(commands.get(i));

      if (i < commands.size() - 1)
      {
        printableCommandBuilder.append(" ");
      }
    }

    return printableCommandBuilder.toString();
  }

}
