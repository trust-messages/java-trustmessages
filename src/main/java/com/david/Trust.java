/**
 * This class file was automatically generated by jASN1 v1.6.0 (http://www.openmuc.org)
 */

package com.david;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import org.openmuc.jasn1.ber.*;
import org.openmuc.jasn1.ber.types.*;
import org.openmuc.jasn1.ber.types.string.*;


public class Trust {

	public static final BerIdentifier identifier = new BerIdentifier(BerIdentifier.UNIVERSAL_CLASS, BerIdentifier.CONSTRUCTED, 16);
	protected BerIdentifier id;

	public byte[] code = null;
	public SystemIdentity tms = null;

	public Entity target = null;

	public Service service = null;

	public BinaryTime date = null;

	public BerAny value = null;

	public Trust() {
		id = identifier;
	}

	public Trust(byte[] code) {
		id = identifier;
		this.code = code;
	}

	public Trust(SystemIdentity tms, Entity target, Service service, BinaryTime date, BerAny value) {
		id = identifier;
		this.tms = tms;
		this.target = target;
		this.service = service;
		this.date = date;
		this.value = value;
	}

	public int encode(BerByteArrayOutputStream os, boolean explicit) throws IOException {

		int codeLength;

		if (code != null) {
			codeLength = code.length;
			for (int i = code.length - 1; i >= 0; i--) {
				os.write(code[i]);
			}
		}
		else {
			codeLength = 0;
			codeLength += value.encode(os);
			
			codeLength += date.encode(os, true);
			
			codeLength += service.encode(os, true);
			
			codeLength += target.encode(os, true);
			
			codeLength += tms.encode(os, true);
			
			codeLength += BerLength.encodeLength(os, codeLength);
		}

		if (explicit) {
			codeLength += id.encode(os);
		}

		return codeLength;

	}

	public int decode(InputStream is, boolean explicit) throws IOException {
		int codeLength = 0;
		int subCodeLength = 0;
		BerIdentifier berIdentifier = new BerIdentifier();

		if (explicit) {
			codeLength += id.decodeAndCheck(is);
		}

		BerLength length = new BerLength();
		codeLength += length.decode(is);

		int totalLength = length.val;
		codeLength += totalLength;

		subCodeLength += berIdentifier.decode(is);
		if (berIdentifier.equals(SystemIdentity.identifier)) {
			tms = new SystemIdentity();
			subCodeLength += tms.decode(is, false);
			subCodeLength += berIdentifier.decode(is);
		}
		else {
			throw new IOException("Identifier does not match the mandatory sequence element identifer.");
		}
		
		if (berIdentifier.equals(Entity.identifier)) {
			target = new Entity();
			subCodeLength += target.decode(is, false);
			subCodeLength += berIdentifier.decode(is);
		}
		else {
			throw new IOException("Identifier does not match the mandatory sequence element identifer.");
		}
		
		if (berIdentifier.equals(Service.identifier)) {
			service = new Service();
			subCodeLength += service.decode(is, false);
			subCodeLength += berIdentifier.decode(is);
		}
		else {
			throw new IOException("Identifier does not match the mandatory sequence element identifer.");
		}
		
		if (berIdentifier.equals(BinaryTime.identifier)) {
			date = new BinaryTime();
			subCodeLength += date.decode(is, false);
		}
		else {
			throw new IOException("Identifier does not match the mandatory sequence element identifer.");
		}
		
		value = new BerAny();
		subCodeLength += value.decode(is, totalLength - subCodeLength);
		return codeLength;
		
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
		encode(os, false);
		code = os.getArray();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("SEQUENCE{");
		sb.append("tms: ").append(tms);
		
		sb.append(", ");
		sb.append("target: ").append(target);
		
		sb.append(", ");
		sb.append("service: ").append(service);
		
		sb.append(", ");
		sb.append("date: ").append(date);
		
		sb.append(", ");
		sb.append("value: ").append(value);
		
		sb.append("}");
		return sb.toString();
	}

}

