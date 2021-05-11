package com.ashelkov.wallet.bip.wallet;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.address.MoneroAddress;
import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.util.DigestUtils;
import com.ashelkov.wallet.bip.util.Ed25519Utils;
import com.ashelkov.wallet.bip.util.EncodingUtils;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;
import static com.ashelkov.wallet.bip.util.DigestUtils.KECCAK_256;
import static com.ashelkov.wallet.bip.util.EncodingUtils.bytesToHex;

public class MoneroWallet extends Wallet {

    // Network byte represents which network the byte is for: 18 = mainnet, 53 = testnet
    public static final byte NETWORK_BYTE = 0x12;

    private static final EdDSAParameterSpec ED_DSA_PARAMETER_SPEC =
            EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

    private final byte[] seed;

    public MoneroWallet(byte[] seed) {

        super(Coin.XMR);

        this.seed = seed;
    }

    @Override
    public Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex) {

        if (account == null) {
            logMissing(ACCOUNT, coinName);
            account = 0;
        }
        if (change != null) {
            logWarning(CHANGE, coinName, change);
        }
        if (addressIndex != null) {
            logWarning(INDEX, coinName, addressIndex);
        }

        return getAddress(account);
    }

    @Override
    public Bip44Address getDefaultAddress(int index) {
        return getAddress(index);
    }

    private Bip44Address getAddress(int account) {

        int[] derivedKeyPath = getDerivedKeyPath(account);
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        byte[] privateSpendKey = Ed25519Utils.reduce32(Bip44Utils.deriveEd25519PrivateKey(seed, derivedKeyPath));
        // TODO: Need better solution for this
        byte[] privateViewKey;
        try {
            privateViewKey = Ed25519Utils.reduce32(DigestUtils.digest(KECCAK_256, privateSpendKey));
        } catch (Exception e) {
            privateViewKey = new byte[0];
        }
        byte[] publicSpendKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateSpendKey).toByteArray();
        byte[] publicViewKey = ED_DSA_PARAMETER_SPEC.getB().scalarMultiply(privateViewKey).toByteArray();

        logger.info(bytesToHex(privateSpendKey));
        logger.info(bytesToHex(privateViewKey));
        logger.info(bytesToHex(publicSpendKey));
        logger.info(bytesToHex(publicViewKey));

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

        return new MoneroAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int account) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, 0};
    }
}
