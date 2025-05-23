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

There are four methods to use this tool:

- Docker (released build)
- Docker (build-it-yourself)
- Released build
- Build it yourself

Docker is likely the preferred way to run the tool for most users (particularly for Windows users). Note:
1. The safest way to use the tool is to compile it yourself - either in your local environment, or using Docker
2. The process for building from source is documented only for Linux and macOS

### Docker

1. Install [Docker](https://docs.docker.com/get-docker/)
2. Ready to go!

The default Docker command for the tool is:

```shell
docker run --rm -it -v ./:/app/output/:rw ashelkov/owg:latest
```

This command will use the Docker image of the official release from DockerHub. However, it's also possible to use a local version of the Docker image. To do so, first build the Docker image from the local code using the helper script:

```shell
./bin/docker/build.sh
```

The command to run this image is the same as the command above, but now referencing the local image:

```shell
docker run --rm -it -v ./:/app/output/:rw owg:latest
```

To make this easier, two helper scripts are included: `bin/docker/local.sh` and `bin/docker/release.sh`. These scripts will run the above local / release Docker commands. Using the above Docker commands requires manually mounting the location to which OWG will write output, whereas these helper scripts will take care of it automatically. Use the scripts as you would normally use OWG:

```shell
./bin/docker/local.sh -o my/wallets/folder -F my-wallet -p solo BTC
```

**NOTE:** When using the Docker helper scripts, the argument to `-o / --output-file` must always be a directory, not a file path. To control the name of the output file, use the `-F / --output-filename` option instead.

### Released Build

1. Install [OpenJDK 16.0.1](https://jdk.java.net/archive/)
2. Download the [latest release](https://github.com/ashelkovnykov/offline-wallet-generator/releases)
3. Ready to go!

The default release build command for the tool is:

```shell
./owg.sh -h
```

### Build it Yourself

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
    -F, --output-filename
      Custom filename for the output file (without extension)
    -o, --output-file
      Path for output files (directory path or complete filepath with extension)
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
# Docker
docker run --rm -it -v /home/user/wallets/:/app/output/:rw ashelkov/owg:latest -m -p solo -n 10 DOGE

# Release build
./owg.sh -m -p solo -n 10 DOGE

# Local build
./bin/local.sh -m -p solo -n 10 DOGE
```

Generate a wallet file for the Bitcoin addresses `m/84'/0'/2'/1'/3'` and `m/84'/0'/2'/1'/4'` using a random 12-word
mnemonic, no password, and a custom output directory:

```shell
# Docker with custom output directory
docker run --rm -it -v /home/user/wallets/:/app/output/:rw ashelkov/owg:latest -o /app/output solo -n 2 BTC -a 2 -c 1 -i 3

# Release build
./owg.sh -e 128 -o ~/wallets/ solo -n 2 BTC --account=2 --change=1 --index=3

# Local build
./bin/local.sh -e 128 -o ~/wallets/ solo -n 2 BTC --account 2 --change 1 -i 3
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

Generate a Bitcoin wallet with a custom filename:

```shell
# Docker
docker run --rm -it -v /home/user/wallets/:/app/output/:rw ashelkov/owg:latest -F my-bitcoin-wallet solo BTC

# Release build
./owg.sh -F my-bitcoin-wallet solo BTC

# Local build
./bin/local.sh -F my-bitcoin-wallet solo BTC
```

## Output File Options

There are three flexible ways to specify where wallet files are saved:

### 1. Specify a Directory

Use the `-o/--output-file` flag to specify a directory where the wallet file will be saved:

```shell
-o ~/wallets/
```

The filename will be automatically generated based on the coin type (e.g., `BTC.wal`, `multi.wal`), or you can customize it with the `-F` flag.

### 2. Specify a Custom Filename 

Use the `-F/--output-filename` flag to specify just the filename (without extension):

```shell
-F my-custom-wallet
```

This will save the file with your custom name in the default directory for your operating system.

### 3. Specify a Complete File Path

You can now provide a complete path including the filename and extension with the `-o` flag:

```shell
-o ~/wallets/my-bitcoin-2025.wal
```

The application will recognize this as a complete file path and use it as is.

### 4. Combine Directory and Custom Filename

You can combine the `-o` and `-F` flags to specify both the directory and filename:

```shell
-o ~/wallets/ -F my-custom-wallet
```

This would save the file as `~/wallets/my-custom-wallet.wal`.

### Docker Simplified Usage

When using Docker, the path handling has been improved so you don't need to worry about matching paths exactly. Just make sure your volume is mounted correctly:

```shell
docker run --rm -it -v /host/path:/app/output/:rw ashelkov/owg:latest -F my-wallet solo BTC
```

This will automatically save the file to `/host/path/my-wallet.wal` on your host machine.

### Default Behavior

If no output options are specified, the default behavior is to save the file in the OS-specific default wallet directory with the coin name as the filename:

- Linux: `$HOME/.wallets/{coin}.wal`
- MacOS: `$HOME/Library/Wallets/{coin}.wal`
- Windows: `%APPDATA%\Wallets\{coin}.wal`

Where `{coin}` is the cryptocurrency code (for single-coin wallets) or `multi` (for a multi-coin wallet).
