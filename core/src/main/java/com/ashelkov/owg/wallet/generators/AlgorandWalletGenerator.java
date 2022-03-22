package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.stellar.sdk.KeyPair;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.AlgorandWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.ALGO;
import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;
import static com.ashelkov.owg.wallet.util.DigestUtils.SHA_512_256;

/**
 * Factory class to generate [[AlgorandWallet]] objects.
 */
public class AlgorandWalletGenerator extends AccountWalletGenerator {

    private static final int ADDRESS_LENGTH = 58;

    private static final byte[] ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '2', '3', '4', '5', '6', '7'};

    public AlgorandWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
    }

    /**
     * Generate the default [[AlgorandWallet]] ('account' field has default value).
     *
     * @return New Algorand wallet containing only the address m/44'/283'/0'
     */
    @Override
    public AlgorandWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new AlgorandWallet(wrapper);
    }

    /**
     * Generate an [[AlgorandWallet]] for a particular BIP-44 'account'. Optionally, generate more than one address by
     * incrementing the 'account' field.
     *
     * @param account Account value
     * @param numAddresses Number of addresses to generate
     * @return New Algorand wallet
     */
    @Override
    public AlgorandWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new AlgorandWallet(addresses);
    }

    /**
     * Generate the Algorand address for a particular BIP-44 'account'.
     *
     * @param account Account value
     * @return Algorand address
     */
    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);
        byte[] publicKey = KeyPair.fromBip39Seed(seed, account).getPublicKey();
        byte[] checksum = DigestUtils.unsafeDigest(SHA_512_256, publicKey);
        byte[] unencodedAddress = new byte[publicKey.length + CHECKSUM_LENGTH];
        System.arraycopy(publicKey, 0, unencodedAddress, 0, publicKey.length);
        System.arraycopy(
                checksum,
                (checksum.length - CHECKSUM_LENGTH),
                unencodedAddress,
                publicKey.length,
                CHECKSUM_LENGTH);

        byte[] encodedAddress = EncodingUtils.to5BitBytesSafe(unencodedAddress);
        // Need to filter down to 58 bytes
        byte[] addressBytes = new byte[ADDRESS_LENGTH];
        for (int i = 0; i < ADDRESS_LENGTH; ++i) {
            addressBytes[i] = ENCODE_TABLE[encodedAddress[i]];
        }

        String address = StringUtils.newStringUtf8(addressBytes);

        return new BIP44Address(address, addressPath);
    }

    /**
     * Generate the full BIP-44 path for a given Algorand account value.
     *
     * @param account Account value
     * @return BIP-44 path for the given account
     */
    private int[] getAddressPath(int account) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = ALGO.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
