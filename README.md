# eclipse-cdt
[![DOI](https://zenodo.org/badge/doi/10.5061/dryad.5q0g45h.svg)](https://doi.org/10.5061/dryad.5q0g45h)

Shaun C D’Souza. Eclipse cdt code analysis and unit testing. PeerJ Preprints, 6:e27350v1, 2018. url: https://peerj.com/preprints/27350/

# System requirements
* Install Java JDK
* Set %JAVA_HOME% to jdk path

# Install Eclipse Luna
* Download Eclipse IDE for C/C++ Developers (Luna)
	* Windows 64-bit
	* https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/luna/SR2/eclipse-cpp-luna-SR2-win32-x86_64.zip
	* Unzip in folder
	* Set %ECLIPSE_HOME% to Eclipse folder path

# Generate Eclipse dropin jar
* Navigate to jar folder
```
cd jar
```

* Execuate make batch script
```
..\make.bat
```

* Copy generated [..\fakeunittest_1.0.jar](/fakeunittest_1.0.jar) in %ECLIPSE_HOME%\dropins

* Start Eclipse. You should see a FakeSources button / Menu option (see [images/eclipse_cdt.jpg](/images/eclipse_cdt.jpg))

# Generate Project Fake classes 
* Create new C/C++ Project in Eclipse 'testcpp'
	* Copy files, sub-directories in [testcpp/*](/eclipse-workspace/testcpp) in C:\Users\user\eclipse-workspace\testcpp folder
```
xcopy eclipse-workspace\testcpp\* c:\Users\user\eclipse-workspace\testcpp\ /E
```
* Update sample [eclipse-workspace\testcpp\source.txt](/eclipse-workspace/testcpp/source.txt) in project folder if using a new project name
	1. First line = #project name [testcpp](/eclipse-workspace/testcpp)
	1. Second line = source file name [src\b.cpp](/eclipse-workspace/testcpp/src/b.cpp) in [eclipse-workspace\testcpp\src](/eclipse-workspace/testcpp/src) folder

* Click FakeSources button. Navigate and select [source.txt](/eclipse-workspace/testcpp/source.txt) file

* Fake Sources are generated in [eclipse-workspace\testcpp](/eclipse-workspace/testcpp) folder
	* Refer to [debug.log](/expected-output-in-testcpp/debug.log)
	* Expected output is shown in "expected output in testcpp" folder
		* [bTest-debug.cpp](/expected-output-in-testcpp/unittest/bTest-debug.cpp)
		* [Fakeb-debug.cpp](/expected-output-in-testcpp/unittest/Fakeb-debug.cpp)
		* [Fakeb-debug.h](/expected-output-in-testcpp/unittest/Fakeb-debug.h)
		* [debug.log](/expected-output-in-testcpp/debug.log)

![Eclipse CDT](/images/eclipse_cdt.jpg)

