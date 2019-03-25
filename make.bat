
rmdir /S /Q fakeunittest

"%JAVA_HOME%\bin\javac" -cp "%ECLIPSE_HOME%\plugins\*" ..\src\FakeClassTest.java ..\src\ClassFake_cpp.java ..\src\ClassFake_h.java ..\src\ASTVisitorImpl.java -d .

"%JAVA_HOME%\bin\javac" -cp "%ECLIPSE_HOME%\plugins\*;." ..\src\FakeUnitTest.java -d .

REM mkdir icons
REM copy ..\lib\icons\* icons
REM copy ..\lib\plugin.xml .

copy ..\lib\Activator.class .\fakeunittest

"%JAVA_HOME%\bin\jar" cvfm ..\fakeunittest_1.0.jar ..\lib\MANIFEST.MF *

