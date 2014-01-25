itude-mobile-maven-plugin-android-calabash
==========================================

Maven plugin that runs Calabash tests for Android

# Background
With [Calabash](http://calaba.sh) you can write your app tests in a langauge that's easy to understand for anyone. This plugin allows you to configure and run Calabash for Android in your Maven project.

# Prerequisites
This maven plugin requires the following:
1. Ruby is installed
2. Gem calabash-android is installed
3. The command calabash-android is available on the path

# Usage
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
