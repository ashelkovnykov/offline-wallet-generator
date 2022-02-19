# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Upcoming
### Short-term
- Add "output to console" option
- Add "secure output to console" option (print address only)
- Add "output public keys" option
- Add "output private keys" option
- Add Ergo
- Add Chia
- Update all java & import versions to latest
- Refactor terminology from "cold & hot" to "solo & multi"
- Add coin subcommands for "solo" wallets
  - XRP: option to use secp256k1 or ed25519
  - XMR: subaddresses
- Run via Docker
- Unit tests
- Separate API from CLI
- Update documentation

### Long-term
- Generate QR codes
- Alternate output types (YAML, JSON)
- Generate offline transactions

## [Unreleased]
### Added
- XMR support
- Confirmation prompt when choosing a password for your mnemonic phrase
- `xpub` keys for BTC and LTC
- Release jar and convenience scripts
- Option to force overwrite of output file
### Changed
- XRP now uses curve secp256k1 instead of ed25519
- CLI now uses commands instead of options to determine the type of wallet to create (cold/hot)
- Default file name
- Output file no longer overwrites existing file by default

## [0.1.0] - 2021-04-18
### Added
- Initial commit
- Can generate offline wallets using either a random seed or an existing BIP39 mnemonic
- Saves wallet and addresses to plaintext file
- Supports BTC, LTC, DOGE, ETH, XRP, XLM, ALGO, AVAX

[Unreleased]: https://github.com/ashelkovnykov/offline-wallet-generator/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/ashelkovnykov/offline-wallet-generator/releases/tag/v0.1.0
