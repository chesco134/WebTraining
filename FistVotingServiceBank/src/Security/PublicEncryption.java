package Security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class PublicEncryption {

	private static final int AES_KEY_SIZE = 128;
	private Cipher pkCipher;
	private Cipher aesCipher;
	private SecretKeySpec aesKeySpec;
	private byte[] aesKey;
	public byte[] encodedPublicKey;
	
	public PublicEncryption() throws GeneralSecurityException{
		pkCipher = Cipher.getInstance("RSA");
		aesCipher = Cipher.getInstance("AES");
	}
	
	public void encrypt(byte[] inBytes, OutputStream out) throws IOException, InvalidKeyException{
		aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
		ByteArrayInputStream in = new ByteArrayInputStream(inBytes);
		CipherOutputStream os = new CipherOutputStream(out, aesCipher);
		copy(in,os);
		os.close();
	}
	
	public void decrypt(ByteArrayInputStream bain, ByteArrayOutputStream out) throws IOException, InvalidKeyException{
		aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
		CipherInputStream cis = new CipherInputStream(bain, aesCipher);
		copy(cis,out);
		cis.close();
	}
	
	private void copy(InputStream in, OutputStream out) throws IOException{
		int readBytes;
		byte[] bytes = new byte[1024];
		while( (readBytes = in.read(bytes)) != -1 ){
			out.write(bytes,0,readBytes);
		}
	}
	
	public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
	    // read private key to be used to decrypt the AES key
	    byte[] encodedKey = new byte[(int)privateKeyFile.length()];
	    FileInputStream is = new FileInputStream(privateKeyFile);
	    is.read(encodedKey);
	    
	    // create private key
	    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PrivateKey pk = kf.generatePrivate(privateKeySpec);
	   
	    // read AES key
	    pkCipher.init(Cipher.DECRYPT_MODE, pk);
	    aesKey = new byte[AES_KEY_SIZE/8];
	    CipherInputStream cis = new CipherInputStream(new FileInputStream(in), pkCipher);
	    cis.read(aesKey);
	    aesKeySpec = new SecretKeySpec(aesKey, "AES");
	    cis.close();
	    is.close();
	  } 
}

/*****************************************************************************************
 * 
 * 		To use the code, you need corresponding public and private RSA keys.
 * 	RSA keys can be generated using the open source tool OpenSSL. However, 
 * 	you have to be careful to generate them in the format required by the 
 * 	Java encryption libraries. 
 * 	To generate a private key of length 2048 bits:
 * 			openssl genrsa -out private.pem 2048
 * 	To get it into the required (PKCS#8, DER) format: 
 * 			openssl pkcs8 -topk8 -in private.pem -outform DER -out private.der -nocrypt
 * 	To generate a public key from the private key: 
 * 			openssl rsa -in private.pem -pubout -outform DER -out public.der
 *
 * 		An example of how to use the code:
 *		FileEncryption secure = new FileEncryption();
 *		
 *		// to encrypt a file
 *		secure.makeKey();
 *		secure.saveKey(encryptedKeyFile, publicKeyFile);
 *		secure.encrypt(fileToEncrypt, encryptedFile);
 *		
 *		// to decrypt it again
 *		secure.loadKey(encryptedKeyFile, privateKeyFile);
 *		secure.decrypt(encryptedFile, unencryptedFile);
 *
 *****************************************************************************************/