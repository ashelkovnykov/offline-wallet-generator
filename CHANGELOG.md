# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Upcoming
### Short-term
- v1.0.0 release

### Long-term
- Unit tests
- Generate QR codes
- Generate offline transactions

### Looking for Volunteer
- Private/public keys for ALGO
- Alternate output types (YAML, JSON)

## [Unreleased]
### Added
- XMR, HNS, and ERG support
- Confirmation prompt when choosing a password for your mnemonic phrase
- `xpub` keys for BTC, LTC, and HNS
- Release jar and convenience scripts
- Option to force overwrite of output file
- Option to print output to console instead of file
- Option to output private/public keys
- Option to use curve ed25519 or secp256k1 for XRP
- XMR subaddresses
- AVAX C-chain and P-chain support
- Code documentation
### Changed
- Replace CLI "cold"/"hot" options with "solo"/"multi" commands
- Replace coin option with subcommands for "solo" wallets
- Change default file name
- No longer overwrite existing wallet file by default
- Move CLI into separate module

## [0.1.0] - 2021-04-18
### Added
- Initial commit
- Generate offline wallets using either a random seed or an existing BIP39 mnemonic
- Save wallet and addresses to plaintext file
- BTC, LTC, DOGE, ETH, XRP, XLM, ALGO, and AVAX support

[Unreleased]: https://github.com/ashelkovnykov/offline-wallet-generator/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/ashelkovnykov/offline-wallet-generator/releases/tag/v0.1.0
