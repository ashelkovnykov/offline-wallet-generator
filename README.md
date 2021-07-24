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
bin/owg.sh
```

To run the latest unreleased version of the application (using the latest code), build the application from source and
then run the following command:

```shell
bin/owg-local.sh
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
    -h, --help
      Show this usage details page
    -p, --mnemonic-password
      Password for mnemonic used to generate wallet master key
    -o, --output-location
      Output directory or path for wallet file
      Default: /home/ashelkov/.wallets
    -w, --overwrite
      Overwrite wallet if already exists?
      Default: false

  Commands:
    cold      Generate a wallet for a single cryptocurrency
      Usage: cold [options]
        Options:
          -a, --account
            BIP 44 account field for address
          -i, --address-index
            BIP 44 index field for address
          -g, --change
            BIP 44 change field for address
        * -c, --coin
            Crypto currency code of coin for which to generate wallet
            Possible Values: [BTC, LTC, DOGE, ETH, XMR, XRP, XLM, ALGO, AVAX]
          -n, --num-addresses
            Number of addresses to generate
            Default: 1

    hot      Generate a wallet for multiple cryptocurrencies
      Usage: hot
```

#### Examples

Generate a cold wallet for the first 10 Dogecoin addresses, using a custom mnemonic and password:

```shell
bin/owg.sh \
-o ~/.wallets/cold/doge.wal \
-m \
-p \
cold \
--coin=DOGE \
-n=10
```

Generate a cold wallet for Bitcoin addresses `m/84'/0'/2'/1'/3'` and `m/84'/0'/2'/1'/4'` using a random 12-word mnemonic
and no password:

```shell
bin/owg.sh \
-o ~/.wallets/cold/btc.wal \
-e 128 \
cold \
-c=BTC \
--account=2 \
--change=1 \
--index=3 \
--num-addresses=2
```

Generate a hot wallet file containing the default address for every supported coin:

```shell
bin/owg.sh \
-o ~/.wallets/hot.wal \
-m \
-p \
hot
```

### Output

This tool generates the mnemonic, the specified address(es), and its/their BIP 44/84 path(s). Unless a custom name and
location are specified, the wallet will be written to one of the locations specified below, determined by operating
system. The name of the file will be `{coin}.wal`, where `{coin}` is the cryptocurrency code of the wallet, if its a
cold wallet. The name of the file will be `hot.wal` if its a hot wallet.

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
