rd /s /q C:\projects\Grendel-Scan\src-tmp
md C:\projects\Grendel-Scan\src-tmp\src\
cd C:\projects\Grendel-Scan\src-tmp\src\
xcopy /s /e C:\projects\Grendel-Scan\src\* .
xcopy /s /e C:\projects\cobra-Grendel\src\* .
cd ..
zip -9 -r -m Grendel-Scan-%1-src.zip *
mkdir d\docs
cd d\docs
xcopy /s /e c:\projects\Grendel-Scan\docs\* .
cd ..
zip -9 -r -m ..\Grendel-Scan-%1-javadoc.zip *
