@ECHO OFF
REG DELETE HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Run /v Kogger1 /F
REG DELETE HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Run /v Kogger2 /F
REG DELETE HKLM\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Run /v Kogger3 /F
REG DELETE HKLM\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Run /v Kogger4 /F
DEL /F /Q "C:\WINDOWS\syscfg.zip"
DEL /F /Q "C:\WINDOWS\syscfg\*.*"