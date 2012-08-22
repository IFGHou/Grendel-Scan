mkdir C:\projects\Grendel-Scan\distro
cd C:\projects\Grendel-Scan\distro
rd . /s /q
copy C:\projects\Grendel-Scan\release_notes.txt .
xcopy /i C:\projects\Grendel-Scan\Nikto\db_*.* .\Nikto
xcopy /i /s C:\projects\Grendel-Scan\conf .\conf
xcopy /i C:\projects\Grendel-Scan\help\*.html .\help
mkdir bin
cd bin
copy C:\projects\Grendel-Scan\lib\*.* .
unzip -n *.jar
rd /s /q META-INF
del about.html
del *.jar
rd /s /q about_files
xcopy /i /e C:\projects\Grendel-Scan\build\classes\com com
zip -1 -r ..\bin.zip  *.*
cd ..
rd /s /q bin
copy bin.zip bin-orig.zip
mkdir swt


copy bin-orig.zip bin.zip
echo java -Xms128M -Xmx256M -XX:+HeapDumpOnOutOfMemoryError -classpath bin.zip com.grendelscan.GrendelScan $* > grendel.sh
copy c:\projects\Grendel-Scan\lib\swt\org.eclipse.swt.gtk.linux.x86_3.3.2.v3349.jar swt
cd swt
unzip org.eclipse.swt.gtk.linux.x86_3.3.2.v3349.jar
zip -1 -r ..\bin.zip org
zip -1 -r ..\bin.zip *.so
rd . /s /q
cd ..
zip -9 -r Grendel-Scan-%1-linux.zip conf Nikto scan-records bin.zip *.sh help



copy bin-orig.zip bin.zip
echo java -Xms128M -Xmx256M -XstartOnFirstThread -XX:+HeapDumpOnOutOfMemoryError -classpath bin.zip com.grendelscan.GrendelScan $* > grendel.sh
copy c:\projects\Grendel-Scan\lib\swt\org.eclipse.swt.carbon.macosx_3.3.3.v3349.jar swt
cd swt
unzip org.eclipse.swt.carbon.macosx_3.3.3.v3349.jar
zip -1 -r ..\bin.zip org
zip -1 -r ..\bin.zip *.jnilib
rd . /s /q
cd ..
zip -9 -r Grendel-Scan-%1-mac.zip conf Nikto scan-records bin.zip *.sh help


echo @start javaw -Xms128M -Xmx256M -XX:+HeapDumpOnOutOfMemoryError -classpath bin.zip com.grendelscan.GrendelScan %%* > grendel.bat
copy c:\projects\Grendel-Scan\lib\swt\org.eclipse.swt.win32.win32.x86_3.3.3.v3349.jar swt
cd swt
unzip org.eclipse.swt.win32.win32.x86_3.3.3.v3349.jar
zip -1 -r ..\bin.zip org
zip -1 -r ..\bin.zip *.dll
rd . /s /q
cd ..
zip -9 -r Grendel-Scan-%1-win32.zip conf Nikto scan-records bin.zip *.bat help


del bin-orig.zip


rd /s /q C:\projects\Grendel-Scan\src-tmp
md C:\projects\Grendel-Scan\src-tmp\src\
cd C:\projects\Grendel-Scan\src-tmp\src\
xcopy /s /e C:\projects\Grendel-Scan\src\* .
cd ..
zip -9 -r -m Grendel-Scan-%1-src.zip *
move Grendel-Scan-%1-src.zip C:\projects\Grendel-Scan\distro
rd /s /q C:\projects\Grendel-Scan\src-tmp

rd /s /q C:\projects\Grendel-Scan\docs-tmp
md C:\projects\Grendel-Scan\src-tmp\docs\
cd C:\projects\Grendel-Scan\src-tmp\docs\
xcopy /s /e c:\projects\Grendel-Scan\docs\* .
cd ..
zip -9 -r -m Grendel-Scan-%1-javadoc.zip *
move Grendel-Scan-%1-javadoc.zip C:\projects\Grendel-Scan\distro

cd ..\distro
call grendel.bat
cd ..\misc