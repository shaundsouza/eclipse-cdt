# eclipse-cdt

Shaun C D’Souza. Eclipse cdt code analysis and unit testing. PeerJ Preprints, 6:e27350v1,
2018. url: https://peerj.com/preprints/27350/

# System requirements
* Java JDK
* Set %JAVA_HOME% to jdk path

# Install Eclipse Luna
* Download Eclipse IDE for C/C++ Developers (Luna)
	* Windows 64-bit
	* https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/luna/SR2/eclipse-cpp-luna-SR2-win32-x86_64.zip
	* Unzip in folder
	* Set %ECLIPSE_HOME% to Eclipse folder path

# Generate Eclipse dropin jar
	* Navigate to jar folder
    cd jar

	* Execuate make batch script
    ..\make.bat

	* Copy generated ..\fakeunittest_1.0.jar in %ECLIPSE_HOME%\dropins

	* Start Eclipse. You should see a FakeSources button / Menu option (see "eclipse cdt.jpg")

# Generate Project Fake classes 
	* Create C++ Project using template "testcpp" in eclipse-workspace

	* Update sample eclipse-workspace\testcpp\source.txt in project folder
	1. First line = target project name (testcpp)
	1. Second line = target source file (b.cpp) in eclipse-workspace\testcpp\src folder

	* Click FakeSources button. Navigate and select source.txt file

	* Fake Sources are generated in eclipse-workspace\testcpp folder
		* Refer to debug.log
		* Expected output is shown in "expected output in testcpp" folder
    bTest-debug.cpp
    Fakeb-debug.cpp
    Fakeb-debug.h
    debug.log

