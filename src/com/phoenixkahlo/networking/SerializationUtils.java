package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SerializationUtils {

	private SerializationUtils() {
	}

	/**
	 * @param type
	 *            the type of object to decode
	 * @param decoder
	 *            the decoder to use if the type is not primitive, String, or
	 *            byte[]. may be null
	 * @throws IllegalArgumentException
	 *             if the type is unreadable by SerializationUtils and decoder
	 */
	public static Object readType(Class<?> type, InputStream in, Decoder decoder)
			throws IOException, ProtocolViolationException, IllegalArgumentException {
		if (type == short.class || type == Short.class)
			return readShort(in);
		else if (type == int.class || type == Integer.class)
			return readInt(in);
		else if (type == long.class || type == Long.class)
			return readLong(in);
		else if (type == char.class || type == Character.class)
			return readChar(in);
		else if (type == float.class || type == Float.class)
			return readFloat(in);
		else if (type == double.class || type == Double.class)
			return readDouble(in);
		else if (type == boolean.class || type == Boolean.class)
			return readBoolean(in);
		else if (type == byte.class || type == Byte.class)
			return (byte) in.read();
		else if (type == String.class)
			return readString(in);
		else if (type.isEnum())
			return type.getEnumConstants()[readInt(in)];
		else if (decoder != null)
			return decoder.decode(in);
		else
			throw new IllegalArgumentException();
	}

	/**
	 * @param encoder
	 *            the encoder to use if the type is not primitive, String, or
	 *            byte[]. may be null
	 * @throws IllegalArgumentException
	 *             of encoder throws it or if type not encodable by
	 *             SerializationUtils and encoder
	 */
	public static void writeAny(Object obj, OutputStream out, Encoder encoder)
			throws IOException, IllegalArgumentException {
		if (encoder != null && encoder.canEncode(obj))
			encoder.encode(obj, out);
		else if (obj instanceof Short)
			writeShort((Short) obj, out);
		else if (obj instanceof Integer)
			writeInt((Integer) obj, out);
		else if (obj instanceof Long)
			writeLong((Long) obj, out);
		else if (obj instanceof Character)
			writeChar((Character) obj, out);
		else if (obj instanceof Float)
			writeFloat((Float) obj, out);
		else if (obj instanceof Double)
			writeDouble((Double) obj, out);
		else if (obj instanceof Boolean)
			writeBoolean((Boolean) obj, out);
		else if (obj instanceof Byte)
			out.write((Byte) obj);
		else if (obj instanceof String)
			writeString((String) obj, out);
		else if (obj instanceof Enum)
			writeInt(((Enum<?>) obj).ordinal(), out);
		else
			throw new IllegalArgumentException("cannot encode " + obj);
	}

	public static void writeByteArray(byte[] array, OutputStream out) throws IOException {
		if (array == null) {
			out.write(intToBytes(-1));
		} else {
			out.write(intToBytes(array.length));
			out.write(array);
		}
	}

	public static byte[] readByteArray(InputStream in) throws IOException {
		int length = readInt(in);
		if (length < 0) {
			return null;
		} else {
			byte[] arr = new byte[length];
			in.read(arr);
			return arr;
		}
	}

	public static void writeString(String string, OutputStream out) throws IOException {
		if (string == null)
			out.write(intToBytes(-1));
		else
			writeByteArray(stringToBytes(string), out);
	}

	public static String readString(InputStream in) throws IOException {
		byte[] bytes = readByteArray(in);
		if (bytes == null)
			return null;
		return bytesToString(bytes);
	}

	public static void writeInt(int n, OutputStream out) throws IOException {
		out.write(intToBytes(n));
	}

	public static int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[4];
		in.read(bytes);
		return bytesToInt(bytes);
	}

	public static void writeLong(long n, OutputStream out) throws IOException {
		out.write(longToBytes(n));
	}

	public static long readLong(InputStream in) throws IOException {
		byte[] bytes = new byte[8];
		in.read(bytes);
		return bytesToLong(bytes);
	}

	public static void writeDouble(double n, OutputStream out) throws IOException {
		out.write(doubleToBytes(n));
	}

	public static double readDouble(InputStream in) throws IOException {
		byte[] bytes = new byte[8];
		in.read(bytes);
		return bytesToDouble(bytes);
	}

	public static void writeFloat(float n, OutputStream out) throws IOException {
		out.write(floatToBytes(n));
	}

	public static float readFloat(InputStream in) throws IOException {
		byte[] bytes = new byte[4];
		in.read(bytes);
		return bytesToFloat(bytes);
	}

	public static void writeShort(short n, OutputStream out) throws IOException {
		out.write(shortToBytes(n));
	}

	public static short readShort(InputStream in) throws IOException {
		byte[] bytes = new byte[2];
		in.read(bytes);
		return bytesToShort(bytes);
	}

	public static void writeChar(char c, OutputStream out) throws IOException {
		out.write(charToBytes(c));
	}

	public static char readChar(InputStream in) throws IOException {
		byte[] bytes = new byte[2];
		in.read(bytes);
		return bytesToChar(bytes);
	}

	public static void writeBoolean(boolean b, OutputStream out) throws IOException {
		out.write(b ? 1 : 0);
	}

	public static boolean readBoolean(InputStream in) throws IOException {
		return in.read() != 0;
	}

	public static byte[] intToBytes(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}

	public static int bytesToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public static byte[] longToBytes(long n) {
		return ByteBuffer.allocate(4).putDouble(n).array();
	}

	public static long bytesToLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	public static byte[] doubleToBytes(double n) {
		return ByteBuffer.allocate(8).putDouble(n).array();
	}

	public static double bytesToDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	public static byte[] floatToBytes(float n) {
		return ByteBuffer.allocate(4).putFloat(n).array();
	}

	public static float bytesToFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	public static byte[] shortToBytes(short n) {
		return ByteBuffer.allocate(2).putShort(n).array();
	}

	public static short bytesToShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	public static byte[] charToBytes(char c) {
		return ByteBuffer.allocate(2).putChar(c).array();
	}

	public static char bytesToChar(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getChar();
	}

	/**
	 * Uses UTF-8
	 */
	public static byte[] stringToBytes(String string) {
		return string.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Uses UTF-8
	 */
	public static String bytesToString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
