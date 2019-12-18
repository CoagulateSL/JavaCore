package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author Iain Price
 */
public abstract class CertUtils {

	@Nonnull
	public static RSAPrivateKey generatePrivateKeyFromDER(@Nonnull final byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		final PKCS8EncodedKeySpec spec=new PKCS8EncodedKeySpec(keyBytes);
		final KeyFactory factory=KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) factory.generatePrivate(spec);
	}

	public static byte[] parseDERFromPEM(@Nonnull final byte[] pem,
	                                     @Nonnull final String beginDelimiter,
	                                     @Nonnull final String endDelimiter)
	{
		final String data=new String(pem);
		String[] tokens=data.split(beginDelimiter);
		tokens=tokens[1].split(endDelimiter);
		return DatatypeConverter.parseBase64Binary(tokens[0]);
	}

	@Nonnull
	public static X509Certificate generateCertificateFromDER(@Nonnull final byte[] certBytes) throws CertificateException {
		final CertificateFactory factory=CertificateFactory.getInstance("X.509");
		return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
	}

}
