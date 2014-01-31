itude-mobile-maven-plugin-android-calabash
==========================================

Maven plugin that runs Calabash tests for Android

# Background
With [Calabash](http://calaba.sh) you can write your app tests in a langauge that's easy to understand for anyone. This plugin allows you to configure and run Calabash for Android in your Maven project.

# Prerequisites
This maven plugin requires the following:
- Ruby is installed
- Gem calabash-android is installed
- The command calabash-android is available on the path

# Usage
```xml
<build>
		<defaultGoal>test</defaultGoal>
		
		<plugins>

			<plugin>
            	<groupId>com.itude.mobile.maven.plugins</groupId>
				<artifactId>android-calabash-runner</artifactId>
				<version>${android.calabash.runner.version}</version>
				
				<executions>
		          <execution>
		            <phase>test</phase>
		            <goals>
		              <goal>test</goal>
		            </goals>
		          </execution>
		        </executions>
				
				<configuration>
				    <!-- <command>calabash-android</command> -->
					<!-- <action>run</action> -->
					
				    <apkRootFolder>${apk.root.folder}</apkRootFolder>
					<ignoreFailedTests>true</ignoreFailedTests>
					<!-- <screenshotsDirectory>path/to/screenshots</screenshotsDirectory> -->
					<!-- <verbose>true</verbose> -->
					
					<reports>
					    <report>
					        <format>json</format>
					        <!-- <path>alternative/path/to/reports</path> -->
					        <fileName>calabash-reports.json</fileName>
					    </report>
					    <report>
					        <format>junit</format>
					    </report>
					</reports>
					
					<!--
					<features>
						<feature>../features/options.feature</feature>
						<feature>features/login.feature</feature>						 
					</features>
					<tags>
						<tag>loginError</tag>
						<tag>options</tag>
					</tags>
					-->
					
				</configuration>
					
			</plugin>

		</plugins>
	</build>
```	
## Contribute

If you find a bug or have a new feature you want to add, just create a pull request and submit it to us. You can also [file an issue](https://github.com/ItudeMobile/itude-mobile-maven-plugin-android-calabash/issues/new).

Please note, if you have a pull request, make sure to use the [develop branch](https://github.com/ItudeMobile/itude-mobile-maven-plugin-android-calabash/tree/develop) as your base.

#### Formatting

For contributors using Eclipse there's a [formatter](http://mobbl.org/downloads/code-format.xml) available.

## License
The code in this project is licensed under the Apache Software License 2.0, per the terms of the included LICENSE file.

##Attributions

The initial version was written by [Ali Derbane](https://github.com/aderbane), [Coen Houtman](https://github.com/choutman) and [Wiebe Elsinga](https://github.com/welsinga)
	
