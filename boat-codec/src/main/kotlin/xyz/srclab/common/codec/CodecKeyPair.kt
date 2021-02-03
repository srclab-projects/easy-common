package xyz.srclab.common.codec

/**
 * Key pair of codec.
 *
 * @param <PUB> type of public key
 * @param <PRI> type of private key
</PRI></PUB> */
interface CodecKeyPair<PUB, PRI> {
    /**
     * Returns public key.
     *
     * @return public key
     */
    val publicKey: PUB

    /**
     * Returns private key.
     *
     * @return private key
     */
    val privateKey: PRI

    /**
     * Returns public key as bytes.
     *
     * @return public key as bytes
     */
    val publicKeyBytes: ByteArray?

    /**
     * Returns private key as bytes.
     *
     * @return private key as bytes
     */
    val privateKeyBytes: ByteArray?

    /**
     * Returns public key as string.
     *
     * @return public key as string
     */
    val publicKeyString: String?

    /**
     * Returns private key as string.
     *
     * @return private key as string
     */
    val privateKeyString: String?

    /**
     * Returns public key as hex string.
     *
     * @return public key as hex string
     */
    val publicKeyHexString: String?

    /**
     * Returns private key as hex string.
     *
     * @return private key as hex string
     */
    val privateKeyHexString: String?

    /**
     * Returns public key as base64 string.
     *
     * @return public key as base64 string
     */
    val publicKeyBase64String: String?

    /**
     * Returns private key as base64 string.
     *
     * @return private key as base64 string
     */
    val privateKeyBase64String: String?
}