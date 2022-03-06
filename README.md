# Offline Wallet Generator

## Application Description

Generate offline wallets for several popular cryptocurrencies.

## Installation Instructions

Java is required to build and run this project.

### Build from Source

Clone this repo using the following command:

```shell
git clone git@github.com:ashelkovnykov/offline-wallet-generator.git
```

Inside the project root directory, run:

- Windows: `gradlew.bat build`
- Linux/MacOS: `./gradlew.sh build`

Alternatively, import the project as a Gradle project into Eclipse or IDEA, then build it.

### Release Version

TODO

## Running the Application

The `lib` folder contains the latest release of the offline-wallet-generator application. In addition, the `bin` folder
contains scripts for running the application without using the `java` command. To run the latest release version of the
application, use the following command from the root folder:

```shell
bin/release.sh
```

To run the latest unreleased version of the application (using the latest code), build the application from source and
then run the following command:

```shell
bin/local.sh
```

### Usage

The following documentation is displayed in the console when the wallet application is run with the `-h` or `--help`
option:

```
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
      Possible Values: [CONSOLE, SECURE_CONSOLE, WALLET]
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
      Usage: solo [options]
        Options:
          -a, --account
            BIP 44 account field for address
          -i, --address-index
            BIP 44 index field for address
          -g, --change
            BIP 44 change field for address
        * -c, --coin
            Crypto currency code of coin for which to generate wallet
            Possible Values: [BTC, LTC, DOGE, ETH, XMR, XRP, XLM, ALGO, ERG, HNS, AVAX]
          -n, --num-addresses
            Number of addresses to generate
            Default: 1

    multi      Generate a wallet for multiple cryptocurrencies
      Usage: multi
```

#### Examples

Generate a wallet file for the first 10 Dogecoin addresses using a custom mnemonic, custom password, and the default
output directory:

```shell
./bin/release.sh \
-m \
-p \
solo \
--coin=DOGE \
-n=10
```

Generate a wallet file for the Bitcoin addresses `m/84'/0'/2'/1'/3'` and `m/84'/0'/2'/1'/4'` using a random 12-word
mnemonic, no password, and a custom output directory:

```shell
./bin/release.sh \
-o ~/btc/BTC-1.wal \
-e 128 \
solo \
-c=BTC \
--account=2 \
--change=1 \
--index=3 \
--num-addresses=2
```

Generate a multi-coin wallet containing the default address for every supported coin using a random 24-word mnemonic, no
password, and output the results to the console:

```shell
./bin/release.sh \
-f CONSOLE \
multi
```

### Output

This tool generates the mnemonic, the specified address(es), and its/their BIP 44/84 path(s). Unless a custom name and
location are specified, the wallet will be written to one of the locations specified below, determined by operating
system. The name of the file will be `{coin}.wal`, where `{coin}` is the cryptocurrency code of the wallet, if it's a
single-coin wallet. The name of the file will be `multi.wal` if it's a multi-coin wallet.

#### Linux
```
$HOME/.wallets/
```
#### Mac
```
$HOME/Library/Wallets/
```
#### Windows
```
%APPDATA%\
```

## Acknowledgements

TODO
