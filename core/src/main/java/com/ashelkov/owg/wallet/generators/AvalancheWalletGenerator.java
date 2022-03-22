package com.ashelkov.owg.wallet.generators;

import java.util.*;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;

import com.ashelkov.owg.address.AvalancheAddress;
import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.coin.avax.Chain;
import com.ashelkov.owg.wallet.AvalancheWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.AVAX;
import static com.ashelkov.owg.bip.Constants.HARDENED;

/**
 * Factory class to generate [[AvalancheWallet]] objects.
 */
public class AvalancheWalletGenerator extends IndexWalletGenerator {

    // 3 Base chains in Avalanche: Exchange (X), Platform (P), and Contracts (C)
    private static final Map<Chain, String> CHAIN_CODE_MAP = Map.of(
            Chain.EXCHANGE, "X",
            Chain.PLATFORM, "P");
    private static final String BECH32_HRP = "avax";
    private static final String CHAIN_DELIMITER = "-";
    private static final String PRIVATE_KEY_BASE = "PrivateKey-";
    // [chain code] + [chain delimiter] + [hrp] + "1" + [32 byte address] + [6 byte checksum] = 45
    private static final int ADDRESS_LENGTH = 45;

    private final Bip32ECKeyPair masterKeyPair;
    private final List<Chain> addressChains;

    public AvalancheWalletGenerator(byte[] seed, List<Chain> addressChains, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);

        if (addressChains.isEmpty()) {
            throw new IllegalArgumentException("No chains selected for AVAX wallet");
        }

        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
        this.addressChains = addressChains;
    }

    /**
     * Generate the default [[AvalancheWallet]] ('index' field has default value).
     *
     * @return New Avalanche wallet containing only the address m/44'/9000'/0'/0/0
     */
    @Override
    public AvalancheWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new AvalancheWallet(wrapper);
    }

    /**
     * Generate a [[AvalancheWallet]] for a particular BIP-44 'index'. Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New Avalanche wallet
     */
    @Override
    public AvalancheWallet generateWallet(int index, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new AvalancheWallet(addresses);
    }

    /**
     * Generate the Avalanche addresses for a particular BIP-44 'index'.
     *
     * Avalanche has multiple chains, each of which has its own address for the same BIP-44 path. For more information,
     * see https://support.avalabs.org/en/articles/4596397-what-is-an-address .
     *
     * @param index Index value
     * @return Ergo address
     */
    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);
        Map<Chain, String> addresses = new HashMap<>(addressChains.size());

        for (Chain chain : addressChains) {
            String address;

            if (chain == Chain.CONTRACT) {
                address = Keys.toChecksumAddress(Keys.getAddress(derivedKeyPair));
            } else {
                address = generateBech32Address(derivedKeyPair, chain);
            }

            addresses.put(chain, address);
        }

        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = PRIVATE_KEY_BASE.concat(
                    EncodingUtils.base58Bitcoin(
                            // Some Avalanche wallets can import a wallet using a 33-byte encrypted private key, and
                            // some can't. Therefore, it's safer to use the 32-byte encrypted private key, especially
                            // since the first byte is always 0. However, due to Java BigIntegers being signed in a
                            // dumb way, their byte array is *also* sometimes 33 bytes long. Therefore, the safest
                            // thing to do is to always grab the 33-byte array, and then skip the first byte (In a
                            // horribly inefficient way, obviously - what do you think this is, C?).
                            Arrays.copyOfRange(derivedKeyPair.getPrivateKeyBytes33(), 1, 33),
                            true));
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.base58Bitcoin(
                    derivedKeyPair.getPublicKeyPoint().getEncoded(true),
                    true);
        }

        return new AvalancheAddress(addresses, addressPath, privKeyText, pubKeyText);
    }

    /**
     * Generate the Bech32 encoded text addresses used for the X and P chains on Avalanche. For more information, see
     * https://support.avalabs.org/en/articles/4587392-what-is-bech32 .
     *
     * @param keyPair The private-public key-pair of the address
     * @param chain The Avalanche chain of the address
     * @return Bech32 encoded address
     */
    private String generateBech32Address(Bip32ECKeyPair keyPair, Chain chain) {
        StringBuilder addressBuilder = new StringBuilder(ADDRESS_LENGTH);
        addressBuilder.append(CHAIN_CODE_MAP.get(chain));
        addressBuilder.append(CHAIN_DELIMITER);
        addressBuilder.append(
                Bech32.encode(
                        Bech32.Encoding.BECH32,
                        BECH32_HRP,
                        EncodingUtils.to5BitBytesSafe(
                                Hash.sha256hash160(
                                        keyPair.getPublicKeyPoint().getEncoded(true)))));

        return addressBuilder.toString();
    }

    /**
     * Generate the full BIP-44 path for a given Avalanche index value.
     *
     * @param index Index value
     * @return BIP-44 path for the given index
     */
    private int[] getAddressPath(int index) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = AVAX.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
