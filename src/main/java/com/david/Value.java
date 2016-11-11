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


public class Value {

	public byte[] code = null;
	public Entity source = null;

	public Entity target = null;

	public BinaryTime date = null;

	public Service service = null;

	public Value() {
	}

	public Value(byte[] code) {
		this.code = code;
	}

	public Value(Entity source, Entity target, BinaryTime date, Service service) {
		this.source = source;
		this.target = target;
		this.date = date;
		this.service = service;
	}

	public int encode(BerByteArrayOutputStream os, boolean explicit) throws IOException {
		if (code != null) {
			for (int i = code.length - 1; i >= 0; i--) {
				os.write(code[i]);
			}
			return code.length;

		}
		int codeLength = 0;
		if (service != null) {
			codeLength += service.encode(os, false);
			// write tag: APPLICATION_CLASS, PRIMITIVE, 3
			os.write(0x43);
			codeLength += 1;
			return codeLength;

		}
		
		if (date != null) {
			codeLength += date.encode(os, false);
			// write tag: APPLICATION_CLASS, PRIMITIVE, 2
			os.write(0x42);
			codeLength += 1;
			return codeLength;

		}
		
		if (target != null) {
			codeLength += target.encode(os, false);
			// write tag: APPLICATION_CLASS, PRIMITIVE, 1
			os.write(0x41);
			codeLength += 1;
			return codeLength;

		}
		
		if (source != null) {
			codeLength += source.encode(os, false);
			// write tag: APPLICATION_CLASS, PRIMITIVE, 0
			os.write(0x40);
			codeLength += 1;
			return codeLength;

		}
		
		throw new IOException("Error encoding BerChoice: No item in choice was selected.");
	}

	public int decode(InputStream is, BerIdentifier berIdentifier) throws IOException {
		int codeLength = 0;
		BerIdentifier passedIdentifier = berIdentifier;

		if (berIdentifier == null) {
			berIdentifier = new BerIdentifier();
			codeLength += berIdentifier.decode(is);
		}

		BerLength length = new BerLength();
		if (berIdentifier.equals(BerIdentifier.APPLICATION_CLASS, BerIdentifier.PRIMITIVE, 0)) {
			source = new Entity();
			codeLength += source.decode(is, false);
			return codeLength;
		}

		if (berIdentifier.equals(BerIdentifier.APPLICATION_CLASS, BerIdentifier.PRIMITIVE, 1)) {
			target = new Entity();
			codeLength += target.decode(is, false);
			return codeLength;
		}

		if (berIdentifier.equals(BerIdentifier.APPLICATION_CLASS, BerIdentifier.PRIMITIVE, 2)) {
			date = new BinaryTime();
			codeLength += date.decode(is, false);
			return codeLength;
		}

		if (berIdentifier.equals(BerIdentifier.APPLICATION_CLASS, BerIdentifier.PRIMITIVE, 3)) {
			service = new Service();
			codeLength += service.decode(is, false);
			return codeLength;
		}

		if (passedIdentifier != null) {
			return 0;
		}
		throw new IOException("Error decoding BerChoice: Identifier matched to no item.");
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
		encode(os, false);
		code = os.getArray();
	}

	public String toString() {
		if ( source!= null) {
			return "CHOICE{source: " + source + "}";
		}

		if ( target!= null) {
			return "CHOICE{target: " + target + "}";
		}

		if ( date!= null) {
			return "CHOICE{date: " + date + "}";
		}

		if ( service!= null) {
			return "CHOICE{service: " + service + "}";
		}

		return "unknown";
	}

}

