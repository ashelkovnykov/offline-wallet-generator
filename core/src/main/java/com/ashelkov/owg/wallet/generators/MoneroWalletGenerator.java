package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.MoneroAddress;
import com.ashelkov.owg.wallet.MoneroWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.Ed25519Utils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;
import static com.ashelkov.owg.wallet.util.DigestUtils.KECCAK_256;

public class MoneroWalletGenerator extends AccountWalletGenerator {

    // Network byte represents which network the byte is for: 18 = mainnet, 53 = testnet
    public static final byte NETWORK_BYTE = 0x12;

    private static final EdDSAParameterSpec ED_DSA_PARAMETER_SPEC =
            EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

    private final byte[] seed;
    private final boolean genSpendKey;
    private final boolean genViewKey;

    public MoneroWalletGenerator(byte[] seed, boolean genViewKey, boolean genSpendKey,  boolean genPrivKey) {
        super(genPrivKey, false);
        this.seed = seed;
        this.genViewKey = genViewKey;
        this.genSpendKey = genSpendKey;
    }

    @Override
    public MoneroWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new MoneroWallet(wrapper);
    }

    @Override
    public MoneroWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new MoneroWallet(addresses);
    }

    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);

        byte[] privateSpendKey = Ed25519Utils.reduce32(Ed25519Utils.deriveEd25519PrivateKey(seed, addressPath));
        byte[] privateViewKey = Ed25519Utils.reduce32(DigestUtils.unsafeDigest(KECCAK_256, privateSpendKey));
        byte[] publicSpendKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateSpendKey).toByteArray();
        byte[] publicViewKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateViewKey).toByteArray();

        byte[] rawAddressNoChecksum = new byte[65];
        rawAddressNoChecksum[0] = NETWORK_BYTE;
        System.arraycopy(publicSpendKey, 0, rawAddressNoChecksum, 1, 32);
        System.arraycopy(publicViewKey, 0, rawAddressNoChecksum, 33, 32);

        byte[] checksum = DigestUtils.unsafeDigest(KECCAK_256, rawAddressNoChecksum);
        byte[] rawAddressChecksum = new byte[69];
        System.arraycopy(rawAddressNoChecksum, 0, rawAddressChecksum, 0, 65);
        System.arraycopy(checksum, 0, rawAddressChecksum, 65, CHECKSUM_LENGTH);

        String address = EncodingUtils.base58Monero(rawAddressChecksum);
        
        String publicViewKeyText = null;
        String publicSpendKeyText = null;
        String privateViewKeyText = null;
        String privateSpendKeyText = null;
        if (genViewKey) {
            if (genPrivKey) {
                privateViewKeyText = EncodingUtils.bytesToHex(privateViewKey);
            }
            publicViewKeyText = EncodingUtils.bytesToHex(publicViewKey);
        }
        if (genSpendKey) {
            if (genPrivKey) {
                privateSpendKeyText = EncodingUtils.bytesToHex(privateSpendKey);
            }
            publicSpendKeyText = EncodingUtils.bytesToHex(publicSpendKey);
        }

        return new MoneroAddress(
                address,
                addressPath,
                privateViewKeyText,
                privateSpendKeyText,
                publicViewKeyText,
                publicSpendKeyText);
    }

    private int[] getAddressPath(int account) {
        int purpose = MoneroWallet.PURPOSE | HARDENED;
        int coinCode = MoneroWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
