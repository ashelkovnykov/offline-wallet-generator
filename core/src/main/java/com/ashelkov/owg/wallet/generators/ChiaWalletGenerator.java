package com.ashelkov.owg.wallet.generators;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.ChiaWallet;
import com.ashelkov.owg.wallet.util.BLSUtils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class ChiaWalletGenerator extends WalletGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ChiaWalletGenerator.class);
    private static final String BECH32_HRP = "xch";

    private final byte[] seed;

    public ChiaWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
//        super(genPrivKey, genPubKey);
        super(genPrivKey, false);
        this.seed = seed;
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, ChiaWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, ChiaWallet.COIN);
    }

    @Override
    public ChiaWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account != null) {
            logWarning(ACCOUNT, account);
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index == null) {
            logMissing(INDEX);
            index = DEFAULT_FIELD_VAL;
        }

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new ChiaWallet(addresses);
    }

    @Override
    public ChiaWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new ChiaWallet(wrapper);
    }

    private BIP44Address generateAddress(int index) {
        int[] addressPath = getAddressPath(index);
        byte[] masterKey = BLSUtils.deriveParentKey(seed);
        byte[] privKey = BLSUtils.deriveBip44ChildKey(masterKey, addressPath);

        String address = Bech32.encode(BECH32_HRP, EncodingUtils.to5BitBytesSafe(privKey));

        logger.error(EncodingUtils.bytesToHex(EncodingUtils.to5BitBytesSafe(privKey)));

        String privKeyText = null;
        if (genPrivKey) {
            privKeyText = new BigInteger(privKey).toString();
        }

        return new BIP44Address(address, addressPath, null, null);

        // Where I last left off:
        // https://github.com/Chia-Network/chia-blockchain/blob/f2fe1dca6266f9b1f8ab61798ad40e5985f50b23/chia/cmds/keys_funcs.py#L97
    }

    private int[] getAddressPath(int index) {
        int purpose = ChiaWallet.PURPOSE | HARDENED;
        int coinCode = ChiaWallet.COIN.getCode() | HARDENED;
        // https://github.com/Chia-Network/chia-blockchain/blob/34edf6e35e7126afb124037f8a6bb5feda833c32/chia/wallet/derive_keys.py#L11
        int account = 2 | HARDENED;

        return new int[] {purpose, coinCode, account, index | HARDENED};
    }
}
