package com.ashelkov.wallet.bip.wallet;

import com.algorand.algosdk.util.Digester;
import org.apache.commons.codec.binary.StringUtils;
import org.stellar.sdk.KeyPair;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.AlgorandAddress;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.util.EncodingUtils;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class AlgorandWallet extends Wallet {

    private static final int ADDRESS_LENGTH = 58;
    private static final int CHECKSUM_BYTES = 4;

    private static final byte[] ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '2', '3', '4', '5', '6', '7'};

    private final byte[] seed;

    public AlgorandWallet(byte[] seed) {

        super(Coin.ALGO);

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
        byte[] publicKey = KeyPair.fromBip39Seed(seed, account).getPublicKey();

        // TODO: Need better solution for this
        byte[] checksum;
        try {
            checksum = Digester.digest(publicKey);
        } catch (Exception e) {
            checksum = new byte[0];
        }

        byte[] unencodedAddress = new byte[publicKey.length + CHECKSUM_BYTES];
        System.arraycopy(publicKey, 0, unencodedAddress, 0, publicKey.length);
        System.arraycopy(
                checksum,
                (checksum.length - CHECKSUM_BYTES),
                unencodedAddress,
                publicKey.length,
                CHECKSUM_BYTES);

        byte[] encodedAddress = EncodingUtils.to5BitBytesSafe(unencodedAddress);
        // Need to filter down to 58 bytes
        byte[] addressBytes = new byte[ADDRESS_LENGTH];
        for (int i = 0; i < ADDRESS_LENGTH; ++i) {
            addressBytes[i] = ENCODE_TABLE[encodedAddress[i]];
        }

        String address = StringUtils.newStringUtf8(addressBytes);
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new AlgorandAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int account) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
