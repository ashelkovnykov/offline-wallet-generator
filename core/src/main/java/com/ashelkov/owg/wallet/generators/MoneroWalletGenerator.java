package com.ashelkov.owg.wallet.generators;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.i2p.crypto.eddsa.math.GroupElement;
import org.apache.commons.lang3.ArrayUtils;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.MoneroAddress;
import com.ashelkov.owg.wallet.MoneroWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.Ed25519Utils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.XMR;
import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;
import static com.ashelkov.owg.wallet.util.DigestUtils.KECCAK_256;
import static com.ashelkov.owg.wallet.util.Ed25519Utils.ED_25519_CURVE_SPEC;

/**
 * Factory class to generate [[MoneroWallet]] objects.
 */
public class MoneroWalletGenerator extends AccountIndexWalletGenerator {

    // All Monero addresses use the same path, but different seeds (not actually sure if the Monero standard is to end
    // the BIP44 path with a hardened 0 account, though)
    public static final int[] ADDRESS_PATH =  {
            BIP44Address.PURPOSE | HARDENED,
            XMR.getCode() | HARDENED,
            HARDENED
    };
    // "SubAddr" as byte array
    public static final byte[] SUBADDRESS_PREFIX = {
        (byte)0x53,
        (byte)0x75,
        (byte)0x62,
        (byte)0x41,
        (byte)0x64,
        (byte)0x64,
        (byte)0x72,
        (byte)0x00
    };
    // Prefix + Standard Private View Key + Account + Index = 8 + 32 + 4 + 4 = 48
    public static final int SUBADDRESS_BASE_LENGTH = 48;
    // "Network bytes" represent the network and address type
    // Standard addresses:  18 = mainnet, 53 = testnet
    // Subaddresses:        42 = mainnet, 63 = testnet, 36 = stagenet
    public static final byte MAINNET_ADDRESS_NETWORK_BYTE = 0x12;
    public static final byte MAINNET_SUBADDRESS_NETWORK_BYTE = 0x2a;

    private final byte[] seed;
    private final boolean genSpendKey;
    private final boolean genViewKey;

    public MoneroWalletGenerator(
            byte[] seed,
            boolean genViewKey,
            boolean genSpendKey,
            boolean genPrivKey)
    {
        super(seed, genPrivKey, false);
        this.seed = seed;
        this.genViewKey = genViewKey;
        this.genSpendKey = genSpendKey;
    }

    /**
     * Generate the default [[MoneroWallet]] (just the standard address).
     *
     * @return New Ethereum wallet containing only the standard address at path m/84'/44'/128'
     */
    @Override
    public MoneroWallet generateDefaultWallet() {
        return generateWallet(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, 1);
    }

    /**
     * Generate a [[MoneroWallet]] for the standard address or a particular subaddress. Optionally, generate more than
     * one subaddress by incrementing the subaddress index.
     *
     * If multiple subaddresses are desired, but the 'account' and 'index' fields both start at 0, then include the
     * standard address. Do not count it towards the total.
     *
     * @param account Subaddress account value
     * @param index Subaddress index value
     * @param numAddresses Number of subaddresses to generate
     * @return New Monero wallet
     */
    @Override
    public MoneroWallet generateWallet(int account, int index, int numAddresses) {
        // If the user wants more than 1 address, they actually want subaddresses. However, subaddress 0,0 is the
        // standard address, so we need to increment numAddresses by 1.
        if ((account == 0) && (index == 0) && (numAddresses > 1)) {
            numAddresses += 1;
        }
        List<BIP44Address> addresses = new ArrayList<>(numAddresses);
        boolean hasSubaddresses = (numAddresses > 1) || (account != 0) || (index != 0);

        byte[] privateSpendKey = Ed25519Utils.reduce32(Ed25519Utils.deriveEd25519PrivateKey(seed, ADDRESS_PATH));
        byte[] privateViewKey = Ed25519Utils.reduce32(DigestUtils.unsafeDigest(KECCAK_256, privateSpendKey));
        GroupElement publicSpendKey = ED_25519_CURVE_SPEC.getB().scalarMultiply(privateSpendKey);
        GroupElement publicViewKey = ED_25519_CURVE_SPEC.getB().scalarMultiply(privateViewKey);

        for(int i = index; i < (index + numAddresses); ++i) {

            MoneroAddress address;
            int[] subaddressPath = getSubaddressPath(account, i);

            if ((account == 0) && (i == 0)) {
                address = generateMoneroAddress(
                        MAINNET_ADDRESS_NETWORK_BYTE,
                        subaddressPath,
                        publicSpendKey.toByteArray(),
                        publicViewKey.toByteArray());
            } else {
                address = generateSubaddress(
                        subaddressPath,
                        publicSpendKey,
                        privateViewKey,
                        account,
                        i);
            }

            addresses.add(address);
        }

        String privateSpendKeyText = null;
        String privateViewKeyText = null;
        if (genPrivKey) {
            if (genSpendKey) {
                privateSpendKeyText = EncodingUtils.bytesToHex(privateSpendKey);
            }
            if (genViewKey) {
                privateViewKeyText = EncodingUtils.bytesToHex(privateViewKey);
            }
        }

        return new MoneroWallet(addresses, privateSpendKeyText, privateViewKeyText, hasSubaddresses);
    }

    /**
     * Generate the Monero subaddress for the given 'account' and 'index' values. For more information regarding Monero
     * subaddresses, see https://monerodocs.org/public-address/subaddress/ .
     *
     * @param addressPath Not exactly a BIP-44 path; actually a collection of indices used by the [[MoneroAddress]]
     * @param standardPrivateViewKey Private view key of the Monero standard address
     * @param standardPublicSpendKey Public spend key of the Monero standard address
     * @param account Account for which to generate subaddress
     * @param index Index for which to generate subaddress
     * @return Monero subaddress
     */
    private MoneroAddress generateSubaddress(
            int[] addressPath,
            GroupElement standardPublicSpendKey,
            byte[] standardPrivateViewKey,
            int account,
            int index)
    {
        byte[] mBase = new byte[SUBADDRESS_BASE_LENGTH];
        System.arraycopy(SUBADDRESS_PREFIX, 0, mBase, 0, 8);
        System.arraycopy(standardPrivateViewKey, 0, mBase, 8, 32);
        System.arraycopy(EncodingUtils.intToFourBytes(account, true), 0, mBase, 40, 4);
        System.arraycopy(EncodingUtils.intToFourBytes(index,true ), 0, mBase, 44, 4);

        byte[] mHash = DigestUtils.unsafeDigest(KECCAK_256, mBase);
        ArrayUtils.reverse(mHash);
        byte[] m = new BigInteger(mHash).mod(Ed25519Utils.L).toByteArray();
        ArrayUtils.reverse(m);

        GroupElement D = ED_25519_CURVE_SPEC.getB().scalarMultiply(m).add(standardPublicSpendKey.toCached());
        GroupElement C = ED_25519_CURVE_SPEC
                .getCurve()
                .createPoint(D.toByteArray(), true)
                .scalarMultiply(standardPrivateViewKey);

        return generateMoneroAddress(MAINNET_SUBADDRESS_NETWORK_BYTE, addressPath, D.toByteArray(), C.toByteArray());
    }

    /**
     * Generate a Monero standard address or subaddress from a pair of Monero public keys.
     *
     * @param networkByte The address prefix byte which determines what kind of address this is and from which net
     * @param addressPath Not exactly a BIP-44 path; actually a collection of indices used by the [[MoneroAddress]]
     * @param publicSpendKey Public spend key bytes
     * @param publicViewKey Public view key bytes
     * @return Monero address
     */
    private MoneroAddress generateMoneroAddress(
            byte networkByte,
            int[] addressPath,
            byte[] publicSpendKey,
            byte[] publicViewKey)
    {
        byte[] rawAddressNoChecksum = new byte[65];
        rawAddressNoChecksum[0] = networkByte;
        System.arraycopy(publicSpendKey, 0, rawAddressNoChecksum, 1, 32);
        System.arraycopy(publicViewKey, 0, rawAddressNoChecksum, 33, 32);

        byte[] checksum = DigestUtils.unsafeDigest(KECCAK_256, rawAddressNoChecksum);
        byte[] rawAddressChecksum = new byte[69];
        System.arraycopy(rawAddressNoChecksum, 0, rawAddressChecksum, 0, 65);
        System.arraycopy(checksum, 0, rawAddressChecksum, 65, CHECKSUM_LENGTH);

        String address = EncodingUtils.base58Monero(rawAddressChecksum);

        String publicSpendKeyText = null;
        String publicViewKeyText = null;
        if (genSpendKey) {
            publicSpendKeyText = EncodingUtils.bytesToHex(publicSpendKey);
        }
        if (genViewKey) {
            publicViewKeyText = EncodingUtils.bytesToHex(publicViewKey);
        }

        return new MoneroAddress(address, addressPath, publicSpendKeyText, publicViewKeyText);
    }

    /**
     * Create an ordered set of indices which are used by [[MoneroAddress]].
     *
     * @param account Subaddress account
     * @param index Subaddress index
     * @return Monero "path"
     */
    private int[] getSubaddressPath(int account, int index) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = XMR.getCode() | HARDENED;

        // Since this path is for printing and not key generation, account and index definitely should not be hardened
        return new int[] {purpose, coinCode, account, index};
    }
}
