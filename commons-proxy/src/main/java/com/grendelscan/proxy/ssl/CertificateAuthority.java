package com.grendelscan.proxy.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;

public class CertificateAuthority
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateAuthority.class);

    public synchronized static CertificateAuthority getCertificateAuthority() throws GeneralSecurityException
    {
        if (certificateAuthority == null)
        {
            certificateAuthority = new CertificateAuthority();
        }
        return certificateAuthority;
    }

    public synchronized static void regenerateCA() throws GeneralSecurityException
    {
        LOGGER.info("Regenerating CA");
        try
        {
            getCertificateAuthority().createCAStore();
        }
        catch (IOException e)
        {
            LOGGER.error("Error regenerating CA: " + e.toString(), e);
            throw new GeneralSecurityException(e);
        }
    }

    private final char[] keyPassword;
    private final String caName;
    private final String rootStoreFileName;
    private static final String CA_ALIAS = "CA";
    private X509Certificate caCert;
    private final String certsDir;
    private KeyStore caStore;

    private final HashMap<String, KeyStore> keyStores;

    private final PasswordProtection passwordProtection;
    private static CertificateAuthority certificateAuthority;
    private final X500Principal issuerDN;
    private final SecureRandom randomSource;

    private final KeyPairGenerator keyGenerator;

    /**
     * Private constructors only
     */
    private CertificateAuthority() throws GeneralSecurityException
    {
        LOGGER.trace("Creating certificate authority");
        keyPassword = ConfigurationManager.getString("ca.keypassword", "password").toCharArray();
        caName = ConfigurationManager.getString("ca.ca_name", "Grendel-Scan CA");
        // rootStoreFileName = ConfigurationManager.getString("ca.rootstore_filename", "conf/ca.pfx");
        rootStoreFileName = ConfigurationManager.getString("ca.rootstore_filename", "conf/ca.jks");
        certsDir = ConfigurationManager.getString("ca.certs_directory", "conf/certs/");
        passwordProtection = new KeyStore.PasswordProtection(keyPassword);
        keyStores = new HashMap<String, KeyStore>(2);
        Security.addProvider(new BouncyCastleProvider());
        try
        {

            // issuerDN = new X500Principal("CN=" + caName);
            issuerDN = new X500Principal("C=GI;ST=Gibraltar;L=Gibraltar;O=Ongame Network Ltd;CN=p5-client.ongamenetwork.com;emailAddress=registry@ongame.com");
            keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);

            randomSource = SecureRandom.getInstance("SHA1PRNG");
            randomSource.setSeed(new Date().getTime());

            caStore = getKeyStore(null);
            caCert = (X509Certificate) caStore.getCertificate(CA_ALIAS);
        }
        catch (Exception e)
        {
            String message = "An error occurred when loading or creating the CA: " + e.toString();
            LOGGER.error(message, e);
            throw new GeneralSecurityException(message, e);
        }
    }

    private synchronized void createCAStore() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException
    {
        createKeyStore(rootStoreFileName, null);
    }

    private KeyStore createKeyStore(final String filename, final String hostname) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeyException, NoSuchProviderException, SignatureException,
                    UnrecoverableKeyException
    {
        X509Certificate[] chain;
        if (hostname == null) // is a CA
        {
            LOGGER.trace("Creating CA key store at " + filename);
            chain = new X509Certificate[1];
        }
        else
        // is a host
        {
            LOGGER.trace("Creating host key store at " + filename + " for " + hostname);
            chain = new X509Certificate[2];
            chain[1] = caCert;
        }

        KeyPair keyPair = keyGenerator.generateKeyPair();

        // KeyStore outKeyStore = KeyStore.getInstance("PKCS12");
        KeyStore outKeyStore = KeyStore.getInstance("JKS");
        outKeyStore.load(null);

        if (hostname == null) // is a CA
        {
            caCert = genX509Cert(hostname, 3650, keyPair.getPublic(), keyPair.getPrivate(), null, null); // self-signed, null signer
            chain[0] = caCert;
            outKeyStore.setEntry(CA_ALIAS, new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), chain), passwordProtection);
        }
        else
        // is a host
        {
            PublicKey caPubKey = chain[1].getPublicKey();
            PrivateKey caPrivKey = (PrivateKey) caStore.getKey(CA_ALIAS, getKeyPassword());
            chain[0] = genX509Cert(hostname, 365, keyPair.getPublic(), keyPair.getPrivate(), caPubKey, caPrivKey);
            PrivateKey privateKey = keyPair.getPrivate();
            PrivateKeyEntry pke = new KeyStore.PrivateKeyEntry(privateKey, chain);
            outKeyStore.setEntry(hostname, pke, passwordProtection);
        }
        outKeyStore.store(new FileOutputStream(filename), keyPassword);

        return outKeyStore;
    }

    private X509Certificate genX509Cert(final String commonName, final int validityDays, final PublicKey certPubKey, final PrivateKey certPrivKey, PublicKey caPubKey, PrivateKey caPrivKey) throws NoSuchAlgorithmException, InvalidKeyException,
                    CertificateParsingException, CertificateEncodingException, NoSuchProviderException, SignatureException
    {
        LOGGER.trace("Creating x509 cert for " + commonName);
        boolean isCA = caPubKey == null;
        if (isCA)
        {
            caPubKey = certPubKey;
            caPrivKey = certPrivKey;
        }
        byte[] bSerialNumber = new byte[8];
        randomSource.nextBytes(bSerialNumber);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DAY_OF_YEAR, validityDays);
        Date startDate = new Date(); // time from which certificate is valid
        Date expiryDate = expiry.getTime(); // time after which certificate is not valid
        BigInteger serialNumber = new BigInteger(bSerialNumber).abs(); // serial number for certificate

        X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
        certGenerator.setIssuerDN(issuerDN);
        certGenerator.setSignatureAlgorithm("SHA1withRSA");

        // X500Principal subjectName = new X500Principal("CN=" + commonName);
        X500Principal subjectName = new X500Principal("C=GI;ST=Gibraltar;L=Gibraltar;O=Ongame Network Ltd;CN=p5-client.ongamenetwork.com;emailAddress=registry@ongame.com");
        certGenerator.setSerialNumber(serialNumber);
        // certGenerator.setSerialNumber(new BigInteger("1"));
        certGenerator.setNotBefore(startDate);
        certGenerator.setNotAfter(expiryDate);
        certGenerator.setSubjectDN(subjectName);
        certGenerator.setPublicKey(certPubKey);

        certGenerator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caPubKey));

        certGenerator.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(certPubKey));

        certGenerator.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(isCA));

        return certGenerator.generate(caPrivKey, "BC");
    }

    public char[] getKeyPassword()
    {
        return keyPassword;
    }

    public synchronized KeyStore getKeyStore(final String hostname) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, InvalidKeyException, NoSuchProviderException, SignatureException, KeyStoreException,
                    UnrecoverableKeyException
    {
        KeyStore keyStore = null;
        String filename;
        if (hostname == null) // is a ca
        {
            LOGGER.trace("Getting key store for CA");
            filename = rootStoreFileName;
        }
        else
        // is a host
        {
            LOGGER.trace("Getting key store for host " + hostname);
            if (keyStores.containsKey(hostname))
            {
                return keyStores.get(hostname);
            }
            // filename = certsDir + hostname + ".pfx";
            filename = certsDir + hostname + ".jks";
        }

        if (new File(".", filename).exists())
        {
            LOGGER.trace(filename + " exists, loading key store");
            // keyStore = KeyStore.getInstance("PKCS12");
            keyStore = KeyStore.getInstance("JKS");
            try
            {
                keyStore.load(new FileInputStream(filename), keyPassword);
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to read key store at " + filename, e);
            }
        }
        if (keyStore == null)
        {
            keyStore = createKeyStore(filename, hostname);
        }
        keyStores.put(hostname, keyStore);
        return keyStore;
    }

    public String getRootStoreFileName()
    {
        return rootStoreFileName;
    }
}
