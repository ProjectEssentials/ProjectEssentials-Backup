# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Since 2.0.0 versions change log same for all supported minecraft versions.

## [Unreleased]

## [2.0.0] - 2020-06-09

### Added
- Async world saving added.
- `backup now` command implemented.
- `backup off` command implemented.
- `backup on` command implemented.
- `configure-backup` command implemented.
- Localization added.

### Changed
- Backup settings file path updated.
- load index changed to `6`.
- init block cleanup.
- Providers resolution strategy changed.
- core version updated to 2.0.3.
- core dependency now is not transitive.
- Settings resorted in `BackupConfigurationModel.kt`.

### Fixed
- `BackupConfiguration.kt` incorrect saving fixed.
- Incorrect backup cycle work fixed.
- Pause before backup added.
- Comparing file counts while rolling backup files fixed.
- Random throwable `java.util.ConcurrentModificationException` exception while saving world from coroutine thread.

### Removed
- Redundant argument in launch coroutine removed.

## [2.0.0-SNAPSHOT.1] - 2020-06-02

### Added
- `updatev2.json` added.
- New backup configuration implemented.
- A backup manager re-written.
- Backup file roller re-written.
- `purgeExtensionsExceptions` setting added.
- `purgeNamesExceptions` setting added.
- `notifyPlayersAboutBackup` setting added.
- `zip4j` dependency added.

### Changed
- Backup directory purging improved.
- `removeExtraFiles` renamed to `purgeBackupOutDirectory`.
- License now is MIT!
- .gitignore synced with core module.
- .github files updated.
- `gradle.properties` updated.
- `build.gradle` updated.
- Update file end-point now is v2.
- Core required version now 2.X.
- Credits in `mods.toml` updated.
- Description in `mods.toml` updated.
- Core dependency updated to 2.0.1.

### Fixed
- `ArrayIndexOutOfBoundsException` while generating archive name.

### Removed
- Internal assets removed.
- An internal documentation removed.
- Old source files removed.

## [1.15.2-1.0.1] - 2020-03-18

### Added
- Project Essentials dependencies added to `build.gradle`.

### Changed
- Kotlin version updated to `1.3.70`.
- KotlinX serialization version updated to `0.20.0`. [Fixes crash on server startup]

### Removed
- Essentials dependencies removed from `gradle.properties`.

## [1.14.4-1.0.2] - 2020-03-18

### Added
- Project Essentials dependencies added to `build.gradle`.

### Changed
- Kotlin version updated to `1.3.70`.
- KotlinX serialization version updated to `0.20.0`. [Fixes crash on server startup]

### Removed
- Essentials dependencies removed from `gradle.properties`.

## [1.15.2-1.0.0] - 2020-02-08

### Added
- Initial release.

## [1.14.4-1.0.1] - 2020-01-28

### Fixed
- Make world backup on server causes crash.

## [1.14.4-1.0.0] - 2020-01-26

### Added
- Initial release of Project Essentials Backup as Project Essentials part.
