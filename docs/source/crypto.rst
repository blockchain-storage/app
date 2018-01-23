************
Crypto
************

In order to make the Trustchain Android app cryptographically compatible with `Tribler <https://www.tribler.org>`_, while maintaining its actual functionalities, the `Bouncycastle <https://www.bouncycastle.org>`_ crypto-library has been replaced with `Libsodium <https://download.libsodium.org/doc/>`_. 

To achieve this, `Java JNI bindings <https://github.com/joshjdevl/libsodium-jni>`_ has been used to allow the Trustchain Android app to use native libsodium method invocations. 

Libsodium has been chosen due to the fact that it is used by Tribler, and because it provides all of the core operations needed to build high-level cryptographic tools. It is a portable, cross-compilable, installable, packageable fork of `NaCL <http://nacl.cr.yp.to>`_ with a compatible extended API. Libsodum supports a variety of compilers and operating systems, including Windows (with MingW or Visual Studio, x86 and x64), iOS, Android, as well as Javascript and Webassembly. 

The transition between Bouncycastle and Libsodium allows to switch from the one key pair previously used, to the actual `double key pair <https://download.libsodium.org/doc/public-key_cryptography/>`_. This new format not only contains the public key of the crypto-key pair, but also the public key (used as verify key) from the signing key pair. That is why, before beginning communication, there must be a key exchange between both peers (e.g. Alice and Bob). Supposing that Alice wants to send a message to Bob, she has to encrypt it using Bob's public key, and add an authentication tag using her own private key. Then, when Bob receives it, he checks the message's integrity and decrypts it using his own private key. An adversary would then have to get hold of Alice or Bob's private keys in order to decrypt this message, or construct a different, valid message.


