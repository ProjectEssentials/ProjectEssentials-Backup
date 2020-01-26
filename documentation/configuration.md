# Configuration

## Configuration location

```none
.minecraft/config/ProjectEssentials/backup.json
```

## Configuration description

It configuration file store settings for backup making

## Default configuration

```json
{
    "backupEnabled": true,
    "firstLaunchDelay": true,
    "backupCreationDelay": 120000,
    "backupCompressionLevel": 3,
    "backupDirectoryPath": "backup",
    "backupDateFormat": "yyyy-MM-dd_HH.mm.ss",
    "maxBackupFiles": 10,
    "rollingBackupFilesEnabled": true,
    "removeExtraFiles": true
}
```

## Small documentation :)

Sorry for the very bad English, maybe you can fix it in Pull request.

| Property                   | Type             | Description      |
|---                         |---               |---               |
|`backupEnabled`             |`Boolean`         |If value false then backup loop will be stopped.|
|`firstLaunchDelay`          |`Boolean`         |If value true then backup not will be making at server start. (i.e if value `true` then: `server start -> delay -> backup -> delay`, if value false `then`: `server start -> backup -> delay`)|
|`backupCreationDelay`       |`Long`            |Delay between backup in milliseconds.|
|`backupCompressionLevel`    |`Int`             |Compression level for zip file. Allowed range is `1 - 9` (1 - low compression, 9 - high compression).|
|`backupDirectoryPath`       |`String`          |Path to backup directory.|
|`backupDateFormat`          |`String`          |Date format for backup archive.|
|`maxBackupFiles`            |`Int`             |Maximum backup files in backup directory.|
|`rollingBackupFilesEnabled` |`Boolean`         |New backup file is created periodically, and the old backup file is renamed by appending a date to the name. Each time a new backup file is started, the numbers in the file names of old backup files are increased by one, so the files "rotate" through the dates. Old backup files whose number exceeds a threshold can then will be deleted to save space.|
|`removeExtraFiles`          |`Boolean`         |If value true then extra files will be deleted from backup directory, i.e random files, not backup files.|

## If you have any questions or encounter a problem, be sure to open an [issue](https://github.com/ProjectEssentials/ProjectEssentials-Backup/issues/new/choose)
