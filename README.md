# Offline Wallet Generator

## Purpose

A cryptocurrency "wallet" is the key or keys used to manage one or more cryptocurrency accounts containing funds.

The purpose of this library is to generate wallets for several popular cryptocurrencies "offline" (without access to the
internet, or on a machine which has never been connected to the internet).

The benefit to an "offline" wallet generator is that if you inspect the code and prepare the machine you will use to run
it, you can guarantee the absolute secrecy of the generated wallet. No one but you could possibly know the information
contained within and, depending on how you manage your wallet, it will stay that way forever.

## Philosophy

Choosing to manage your own wallet is one of the most important decisions that you could make. Although it comes with
increased responsibility, it is the only way to truly embrace the freedom that cryptocurrency offers. Any crypto veteran
will tell you the same thing: "not your keys, not your crypto". As convenient as it is to have an exchange or bank
manage your wallet for you (and take a fee for doing so), the risks of having your funds frozen or confiscated are
immense. Though it may seem daunting at first, it has never been easier to take complete autonomy over your finances.
Rise up to the challenge and embrace the financial system that you deserve.

## What to Do With Your Wallet

### Receiving Funds

The addresses in the wallets produced by this tool are yours and ready to receive funds. It is safe to share these
addresses for the purpose of receiving funds.

### Protecting Your Wallet

There are many options for how to protect your wallet:

- Secure the mnemonic (e.g. encrypt it, password protect it, hide it inside an image, etc.) and store it in the cloud
- Secure the mnemonic and store it locally (e.g. USB, external hard drive, etc.)
- Store it in a specialized "hardware wallets"
- Write it down on a piece of paper
- Memorize the mnemonic and delete the files
- Some combination of the above

Each of the above options has advantages and disadvantages in terms of security and convenience. It is ultimately up to
each individual to evaluate his needs and risks. Each individual's attack surface depends on who he is, where he lives,
what he does, and how he uses his crypto.

## Installation

There are three methods to install and use this tool:

- Docker
- Released build
- Build it yourself

Windows users should use Docker. Linux and MacOS users may use whichever method they like. Note that:
1. Building the tool from source is the safest way to use the tool
2. There's nothing stopping Windows users from building the tool from source; the process is just not documented here

### Docker

1. Install [Docker](https://docs.docker.com/get-docker/)
2. Ready to go!

The default Docker command for the tool is:

```shell
docker run --rm -it -v ~/:/app/output/:rw ashelkov/owg:latest
```

### Released Build

1. Install [OpenJDK 16.0.1](https://jdk.java.net/archive/)
2. Download the [latest release](https://github.com/ashelkovnykov/offline-wallet-generator/releases)
3. Ready to go!

The default release build command for the tool is:

```shell
./owg.sh -h
```

### Built it Yourself

1. Install [OpenJDK 16.0.1](https://jdk.java.net/archive/)
2. Pull the latest code using `git`:
    - `git clone https://github.com/ashelkovnykov/offline-wallet-generator.git`
3. Navigate to `./offline-wallet-generator/`
4. Build the tool:
    - `./gradlew.sh clean build`
5. Ready to go!

The default local build command for the tool is:

```shell
./bin/local.sh -h
```

## Usage

The default commands documented above will display the following documentation when run:

```text
Usage: <main class> [options] [command] [command options]
  Options:
    -m, --custom-mnemonic
      Custom mnemonic to use for generating wallets/addresses
    -e, --entropy
      Number of bits of entropy for randomly generated seed (must be 128-256 & 
      multiple of 32)
      Default: 256
    -f, --format
      Generated wallet output format
      Default: WALLET
      Possible Values: [CONSOLE, WALLET]
    -h, --help
      Show this usage details page
    -p, --mnemonic-password
      Password for mnemonic used to generate wallet master key
    -o, --output-file
      Directory or path for output files
      Default: /home/ashelkov/.wallets
    -w, --overwrite
      Overwrite wallet if already exists
      Default: false
    -K, --priv
      Output the private keys for each generated address
      Default: false
    -k, --pub
      Output the public keys for each generated address
      Default: false
  Commands:
    solo      Generate a wallet for a single cryptocurrency
      Usage: solo [options]       [command] [command options]
        Options:
          -n, --num-addresses
            Number of addresses to generate
            Default: 1
        Commands:
          BTC      Generate a Bitcoin wallet
            Usage: BTC [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0
                -c, --change
                  BIP 44 change field for address
                  Default: 0
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          LTC      Generate a Litecoin wallet
            Usage: LTC [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0
                -c, --change
                  BIP 44 change field for address
                  Default: 0
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          DOGE      Generate a Dogecoin wallet
            Usage: DOGE [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0
                -c, --change
                  BIP 44 change field for address
                  Default: 0
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          ETH      Generate an Ethereum wallet
            Usage: ETH [options]
              Options:
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          XMR      Generate a Monero wallet
            Usage: XMR [options]
              Options:
                -s, --spend-key
                  Output the spend key(s)
                  Default: false
                -a, --sub-addr-acc
                  Monero subaddress account
                  Default: 0
                -i, --sub-addr-index
                  Monero subaddress index
                  Default: 0
                -v, --view-key
                  Output the view key(s)
                  Default: false

          XRP      Generate an XRP wallet
            Usage: XRP [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0
                -l, --legacy
                  Use curve secp256k1 instead of ed25519 to generate address
                  Default: false

          XLM      Generate a Stellar wallet
            Usage: XLM [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0

          ALGO      Generate an Algorand wallet
            Usage: ALGO [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0

          ERG      Generate an Ergo wallet
            Usage: ERG [options]
              Options:
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          HNS      Generate a Handshake wallet
            Usage: HNS [options]
              Options:
                -a, --account
                  BIP 44 account field for address
                  Default: 0
                -c, --change
                  BIP 44 change field for address
                  Default: 0
                -i, --index
                  BIP 44 index field for address
                  Default: 0

          AVAX      Generate an Avalanche wallet
            Usage: AVAX [options]
              Options:
                -c, --chains
                  Chains for which to generate address(es)
                  Default: [EXCHANGE]
                -i, --index
                  BIP 44 index field for address
                  Default: 0


    multi      Generate a wallet for multiple cryptocurrencies
      Usage: multi
```

## Examples

Generate a wallet file for the first 10 Dogecoin addresses using a custom mnemonic, custom password, and the default
output directory:

```shell
# Docker (note that Docker always needs a provided output directory)
docker run --rm -it -v /home/user/wallets/:/app/output/:rw ashelkov/owg:latest -m -p solo -n 10 DOGE

# Release build
./owg.sh -m -p solo -n 10 DOGE

# Local build
./bin/local.sh -m -p solo -n 10 DOGE
```

Generate a wallet file for the Bitcoin addresses `m/84'/0'/2'/1'/3'` and `m/84'/0'/2'/1'/4'` using a random 12-word
mnemonic, no password, and a custom output directory:

```shell
# Docker (note that Docker always uses a default file name)
docker run --rm -it -v /home/user/wallets/:/app/output/:rw ashelkov/owg:latest solo -n 2 BTC -a 2 -c 1 -i 3

# Release build
./owg.sh -e 128 -o ~/wallets/my-btc-wallet.wal solo -n 2 BTC --account=2 --change=1 --index=3

# Local build
./bin/local.sh -e 128 -o ~/wallets/my-btc-wallet.wal solo -n 2 BTC --account 2 --change 1 -i 3
```

Generate a multi-coin wallet containing the default address for every supported coin using a random 24-word mnemonic, no
password, and output the results to the console (including public and private keys):

```shell
# Docker (note that the Docker command for console output is slightly different than usual)
docker run --rm -it --entrypoint ./bin/release.sh ashelkov/owg:latest -f CONSOLE -k -K multi

# Release build
./owg.sh -f CONSOLE -k -K multi

# Local build
./bin/local.sh -f CONSOLE -k -K multi
```

## Output

### Docker

If saving to file, Docker always requires an explicit output directory and always uses a default file name for the
wallet.

Printing to console requires a slightly different Docker command:

```shell
docker run --rm --entrypoint ./bin/release.sh ashelkov/owg:latest -f CONSOLE [options]
```

### Release and Local Builds

When saving to a file, if no custom output location is specified then the tool will write the wallet to the default
wallet directory. The name of the file will be `{coin}.{ext}`, where:
- `{coin}` is the cryptocurrency code (for single-coin wallets) or `multi` (for a multi-coin wallet)
- `{ext}` is the file extension of the output type

The default wallet directory is determined by the operating system:
- Linux: `$HOME/.wallets/`
- MacOS: `$HOME/Library/Wallets/`
- Windows: `%APPDATA%\`
