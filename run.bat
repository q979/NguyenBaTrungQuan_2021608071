@echo off
cd /d "%~dp0"
echo Starting app...
java -Xms64m -Xmx192m -cp "target/classes;%USERPROFILE%\.m2\repository\*" com.trungquan.nongsan.NongSanshopwebsiteApplication
