
	Set WshShell = WScript.CreateObject("WScript.Shell")
 	If WScript.Arguments.length = 0 Then
 		Set ObjShell = CreateObject("Shell.Application")
 		ObjShell.ShellExecute "wscript.exe", """" & _
 		WScript.ScriptFullName & """" &_
  		" RunAsAdministrator", , "runas", 1
  	End if

	strDesktop = WshShell.SpecialFolders("Desktop")
	Set objFSO = CreateObject("Scripting.FileSystemObject")

	IF Not objFSO.FileExists(strDesktop & "\taskey.jar") Then
		objFSO.CopyFile "taskey.jar", strDesktop & "\"
	End If

	set oShellLink = WshShell.CreateShortcut(strDesktop & "\Taskey Shortcut.lnk")
        oShellLink.TargetPath = "taskey.jar"
        oShellLink.WindowStyle = 1
        oShellLink.Hotkey = "CTRL+SHIFT+F"
        oShellLink.IconLocation = "notepad.exe, 2"
        oShellLink.Description = "Taskey Shortcut"
        oShellLink.WorkingDirectory = strDesktop
        oShellLink.Save
