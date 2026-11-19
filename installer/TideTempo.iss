; Inno Setup script for TideTempo. Built by installer\build.ps1.
#ifndef AppVersion
  #define AppVersion "1.0.0"
#endif

[Setup]
AppId={{7C1D5B0E-3A52-4E7B-9B4F-2D6A1E8C5F10}
AppName=TideTempo
AppVersion={#AppVersion}
AppPublisher=WigerLabs
DefaultDirName={autopf}\TideTempo
DefaultGroupName=TideTempo
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog
OutputBaseFilename=TideTempo-Setup-{#AppVersion}
SetupIconFile={#IconFile}
UninstallDisplayIcon={app}\TideTempo.exe
UninstallDisplayName=TideTempo
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
CloseApplications=yes

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Shortcuts:"

[Files]
Source: "{#SourceDir}\*"; DestDir: "{app}"; Flags: recursesubdirs createallsubdirs ignoreversion

[Icons]
Name: "{autoprograms}\TideTempo"; Filename: "{app}\TideTempo.exe"
Name: "{autodesktop}\TideTempo"; Filename: "{app}\TideTempo.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\TideTempo.exe"; Description: "Launch TideTempo"; Flags: nowait postinstall skipifsilent

[Code]
procedure StopApp;
var
  rc: Integer;
begin
  Exec(ExpandConstant('{sys}\taskkill.exe'), '/F /T /IM TideTempo.exe', '', SW_HIDE, ewWaitUntilTerminated, rc);
  Sleep(1000);
end;

function PrepareToInstall(var NeedsRestart: Boolean): String;
begin
  StopApp;
  Result := '';
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
begin
  if CurUninstallStep = usUninstall then
    StopApp;
end;

[UninstallDelete]
; Everything the app creates at runtime (logs, saved session) lives here.
Type: filesandordirs; Name: "{localappdata}\TideTempo"
Type: filesandordirs; Name: "{app}"
