package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.stellar.sdk.KeyPair;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.AlgorandWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;
import static com.ashelkov.owg.wallet.util.DigestUtils.SHA_512_256;

public class AlgorandWalletGenerator extends AccountWalletGenerator {

    private static final int ADDRESS_LENGTH = 58;
    private static final int CHECKSUM_BYTES = 4;

    private static final byte[] ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '2', '3', '4', '5', '6', '7'};

    private final byte[] seed;

    public AlgorandWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.seed = seed;
    }

    @Override
    public AlgorandWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new AlgorandWallet(wrapper);
    }

    @Override
    public AlgorandWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new AlgorandWallet(addresses);
    }

    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);
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

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int account) {
        int purpose = AlgorandWallet.PURPOSE | HARDENED;
        int coinCode = AlgorandWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
