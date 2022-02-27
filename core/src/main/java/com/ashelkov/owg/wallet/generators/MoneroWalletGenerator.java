package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.MoneroWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.Ed25519Utils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;
import static com.ashelkov.owg.wallet.util.DigestUtils.KECCAK_256;

public class MoneroWalletGenerator extends WalletGenerator {

    // Network byte represents which network the byte is for: 18 = mainnet, 53 = testnet
    public static final byte NETWORK_BYTE = 0x12;

    private static final EdDSAParameterSpec ED_DSA_PARAMETER_SPEC =
            EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

    private final byte[] seed;

    public MoneroWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.seed = seed;
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, MoneroWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, MoneroWallet.COIN);
    }

    @Override
    public MoneroWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = DEFAULT_FIELD_VAL;
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index != null) {
            logWarning(INDEX, index);
        }

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new MoneroWallet(addresses);
    }

    @Override
    public MoneroWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new MoneroWallet(wrapper);
    }

    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);

        byte[] privateSpendKey = Ed25519Utils.reduce32(Ed25519Utils.deriveEd25519PrivateKey(seed, addressPath));
        // TODO: Need better solution for this
        byte[] privateViewKey;
        try {
            privateViewKey = Ed25519Utils.reduce32(DigestUtils.digest(KECCAK_256, privateSpendKey));
        } catch (Exception e) {
            privateViewKey = new byte[0];
        }
        byte[] publicSpendKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateSpendKey).toByteArray();
        byte[] publicViewKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateViewKey).toByteArray();

        byte[] rawAddressNoChecksum = new byte[65];
        rawAddressNoChecksum[0] = NETWORK_BYTE;
        System.arraycopy(publicSpendKey, 0, rawAddressNoChecksum, 1, 32);
        System.arraycopy(publicViewKey, 0, rawAddressNoChecksum, 33, 32);

        // TODO: Need better solution for this
        byte[] checksum;
        try {
            checksum = DigestUtils.digest(KECCAK_256, rawAddressNoChecksum);
        } catch (Exception e) {
            checksum = new byte[0];
        }

        byte[] rawAddressChecksum = new byte[69];
        System.arraycopy(rawAddressNoChecksum, 0, rawAddressChecksum, 0, 65);
        System.arraycopy(checksum, 0, rawAddressChecksum, 65, 4);

        String address = EncodingUtils.base58Monero(rawAddressChecksum);

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int account) {
        int purpose = MoneroWallet.PURPOSE | HARDENED;
        int coinCode = MoneroWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
