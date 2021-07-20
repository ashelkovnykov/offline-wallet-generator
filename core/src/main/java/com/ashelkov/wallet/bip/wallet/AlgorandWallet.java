package com.ashelkov.wallet.bip.wallet;

import java.util.ArrayList;
import java.util.List;

import com.ashelkov.wallet.bip.util.DigestUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.stellar.sdk.KeyPair;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.AlgorandAddress;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.util.EncodingUtils;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;
import static com.ashelkov.wallet.bip.util.DigestUtils.SHA_512_256;

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
    public List<Bip44Address> generateAddresses(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = 0;
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index != null) {
            logWarning(INDEX, index);
        }

        List<Bip44Address> result = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    @Override
    public List<Bip44Address> generateDefaultAddresses(int numAddresses) {

        List<Bip44Address> result = new ArrayList<>(numAddresses);
        int account = 0;

        for(int i = account; i < (account + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    private Bip44Address getAddress(int account) {

        int[] derivedKeyPath = getDerivedKeyPath(account);
        byte[] publicKey = KeyPair.fromBip39Seed(seed, account).getPublicKey();

        // TODO: Need better solution for this
        byte[] checksum;
        try {
            checksum = DigestUtils.digest(SHA_512_256, publicKey);
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
