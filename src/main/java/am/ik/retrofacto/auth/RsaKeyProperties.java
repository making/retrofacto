package am.ik.retrofacto.auth;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public final class RsaKeyProperties {

	private final RSAPublicKey publicKey;

	private final RSAPrivateKey privateKey;

	public RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public RSAPublicKey publicKey() {
		return publicKey;
	}

	public RSAPrivateKey privateKey() {
		return privateKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (RsaKeyProperties) obj;
		return Objects.equals(this.publicKey, that.publicKey) && Objects.equals(this.privateKey, that.privateKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publicKey, privateKey);
	}

	@Override
	public String toString() {
		return "RsaKeyProperties[" + "publicKey=" + publicKey + ", " + "privateKey=" + privateKey + ']';
	}

}