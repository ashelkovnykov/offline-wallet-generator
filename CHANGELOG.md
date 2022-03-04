# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Upcoming
### Short-term
- Refactor terminology from "cold & hot" to "solo & multi"
- Add coin subcommands for "solo" wallets
  - XRP: option to use secp256k1 or ed25519
  - XMR: view/spend keys
  - XMR: sub-addresses
  - AVAX: P-Chain addresses
- Run via Docker
- Separate API from CLI
- Documentation

### Long-term
- Unit tests
- Generate QR codes
- Generate offline transactions

### Looking for Volunteer
- Private/public keys for ALGO
- Private/public keys for AVAX
- Alternate output types (YAML, JSON)

## [Unreleased]
### Added
- XMR, HNS, and ERG support
- Confirmation prompt when choosing a password for your mnemonic phrase
- `xpub` keys for BTC and LTC
- Release jar and convenience scripts
- Option to force overwrite of output file
- Option to print output to console instead of file
- Option to output private/public keys
### Changed
- Default XRP address  uses curve secp256k1 instead of ed25519
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
