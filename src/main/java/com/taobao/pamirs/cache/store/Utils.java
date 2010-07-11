package com.taobao.pamirs.cache.store;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.View;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

public class Utils
{
	private static NumberFormat	    f;

	private static Map<Class, Byte>	PRIMITIVE_TYPES	                 = new HashMap<Class, Byte>(15);
	private static final byte	    TYPE_NULL	                     = 0;
	private static final byte	    TYPE_STREAMABLE	                 = 1;
	private static final byte	    TYPE_SERIALIZABLE	             = 2;

	private static final byte	    TYPE_BOOLEAN	                 = 10;
	private static final byte	    TYPE_BYTE	                     = 11;
	private static final byte	    TYPE_CHAR	                     = 12;
	private static final byte	    TYPE_DOUBLE	                     = 13;
	private static final byte	    TYPE_FLOAT	                     = 14;
	private static final byte	    TYPE_INT	                     = 15;
	private static final byte	    TYPE_LONG	                     = 16;
	private static final byte	    TYPE_SHORT	                     = 17;
	private static final byte	    TYPE_STRING	                     = 18;
	private static final byte	    TYPE_BYTEARRAY	                 = 19;

	// constants
	public static final int	        MAX_PORT	                     = 65535;	                     // highest
	                                                                                                 // port
	                                                                                                 // allocatable
	static boolean	                resolve_dns	                     = false;

	private static short	        COUNTER	                         = 1;

	private static Pattern	        METHOD_NAME_TO_ATTR_NAME_PATTERN	= Pattern.compile("[A-Z]+");
	private static Pattern	        ATTR_NAME_TO_METHOD_NAME_PATTERN	= Pattern.compile("_.");

	public static List<Address> leftMembers(final View one, final View two)
	{
		if ((one == null) || (two == null))
		{
			return null;
		}
		final List<Address> retval = new ArrayList<Address>(one.getMembers());
		retval.removeAll(two.getMembers());
		return retval;
	}

	public static List<Address> leftMembers(final Collection<Address> old_list,
	        final Collection<Address> new_list)
	{
		if ((old_list == null) || (new_list == null))
		{
			return null;
		}
		final List<Address> retval = new ArrayList<Address>(old_list);
		retval.removeAll(new_list);
		return retval;
	}

	public static List<Address> newMembers(final List<Address> old_list, final List<Address> new_list)
	{
		if ((old_list == null) || (new_list == null))
		{
			return null;
		}
		final List<Address> retval = new ArrayList<Address>(new_list);
		retval.removeAll(old_list);
		return retval;
	}

	/**
	 * Reads and discards all characters from the input stream until a \r\n or
	 * EOF is encountered
	 * 
	 * @param in
	 * @return
	 */
	public static int discardUntilNewLine(final InputStream in)
	{
		int ch;
		int num = 0;

		while (true)
		{
			try
			{
				ch = in.read();
				if (ch == -1)
				{
					break;
				}
				num++;
				if (ch == '\n')
				{
					break;
				}
			} catch (final IOException e)
			{
				break;
			}
		}
		return num;
	}

	public static void objectToStream(final Object obj, final DataOutputStream out) throws Exception
	{
		if (obj == null)
		{
			out.write(TYPE_NULL);
			return;
		}

		Byte type;
		try
		{
			if (obj instanceof Streamable)
			{ // use Streamable if we can
				out.write(TYPE_STREAMABLE);
				writeGenericStreamable((Streamable) obj, out);
			} else if ((type = PRIMITIVE_TYPES.get(obj.getClass())) != null)
			{
				out.write(type.byteValue());
				switch (type.byteValue())
				{
				case TYPE_BOOLEAN:
					out.writeBoolean(((Boolean) obj).booleanValue());
					break;
				case TYPE_BYTE:
					out.writeByte(((Byte) obj).byteValue());
					break;
				case TYPE_CHAR:
					out.writeChar(((Character) obj).charValue());
					break;
				case TYPE_DOUBLE:
					out.writeDouble(((Double) obj).doubleValue());
					break;
				case TYPE_FLOAT:
					out.writeFloat(((Float) obj).floatValue());
					break;
				case TYPE_INT:
					out.writeInt(((Integer) obj).intValue());
					break;
				case TYPE_LONG:
					out.writeLong(((Long) obj).longValue());
					break;
				case TYPE_SHORT:
					out.writeShort(((Short) obj).shortValue());
					break;
				case TYPE_STRING:
					final String str = (String) obj;
					if (str.length() > Short.MAX_VALUE)
					{
						out.writeBoolean(true);
						final ObjectOutputStream oos = new ObjectOutputStream(out);
						try
						{
							oos.writeObject(str);
						} finally
						{
							oos.close();
						}
					} else
					{
						out.writeBoolean(false);
						out.writeUTF(str);
					}
					break;
				case TYPE_BYTEARRAY:
					final byte[] buf = (byte[]) obj;
					out.writeInt(buf.length);
					out.write(buf, 0, buf.length);
					break;
				default:
					throw new IllegalArgumentException("type " + type + " is invalid");
				}
			} else
			{ // will throw an exception if object is not serializable
				out.write(TYPE_SERIALIZABLE);
				final ObjectOutputStream tmp = new ObjectOutputStream(out);
				tmp.writeObject(obj);
			}
		} finally
		{
			Util.close(out);
		}
	}

	public static void writeGenericStreamable(final Streamable obj, final DataOutputStream out)
	        throws IOException, ChannelException
	{
		short magic_number;
		String classname;

		if (obj == null)
		{
			out.write(0);
			return;
		}

		out.write(1);
		magic_number = ClassConfigurator.getMagicNumber(obj.getClass());
		// write the magic number or the class name
		if (magic_number == -1)
		{
			out.writeBoolean(false);
			classname = obj.getClass().getName();
			out.writeUTF(classname);
		} else
		{
			out.writeBoolean(true);
			out.writeShort(magic_number);
		}

		// write the contents
		obj.writeTo(out);
	}

	public static Object objectFromStream(final DataInputStream in) throws Exception
	{
		if (in == null)
		{
			return null;
		}
		Object retval = null;
		final byte b = (byte) in.read();

		switch (b)
		{
		case TYPE_NULL:
			return null;
		case TYPE_STREAMABLE:
			retval = readGenericStreamable(in);
			break;
		case TYPE_SERIALIZABLE: // the object is Externalizable or Serializable
			final ObjectInputStream tmp = new ObjectInputStream(in);
			retval = tmp.readObject();
			break;
		case TYPE_BOOLEAN:
			retval = Boolean.valueOf(in.readBoolean());
			break;
		case TYPE_BYTE:
			retval = Byte.valueOf(in.readByte());
			break;
		case TYPE_CHAR:
			retval = Character.valueOf(in.readChar());
			break;
		case TYPE_DOUBLE:
			retval = Double.valueOf(in.readDouble());
			break;
		case TYPE_FLOAT:
			retval = Float.valueOf(in.readFloat());
			break;
		case TYPE_INT:
			retval = Integer.valueOf(in.readInt());
			break;
		case TYPE_LONG:
			retval = Long.valueOf(in.readLong());
			break;
		case TYPE_SHORT:
			retval = Short.valueOf(in.readShort());
			break;
		case TYPE_STRING:
			if (in.readBoolean())
			{ // large string
				final ObjectInputStream ois = new ObjectInputStream(in);
				try
				{
					retval = ois.readObject();
				} finally
				{
					ois.close();
				}
			} else
			{
				retval = in.readUTF();
			}
			break;
		case TYPE_BYTEARRAY:
			final int len = in.readInt();
			final byte[] tmpbuf = new byte[len];
			in.readFully(tmpbuf, 0, tmpbuf.length);
			retval = tmpbuf;
			break;
		default:
			throw new IllegalArgumentException("type " + b + " is invalid");
		}
		return retval;
	}

	public static Streamable readGenericStreamable(final DataInputStream in) throws IOException
	{
		Streamable retval = null;
		final int b = in.read();
		if (b == 0)
		{
			return null;
		}

		final boolean use_magic_number = in.readBoolean();
		String classname;
		Class clazz;

		try
		{
			if (use_magic_number)
			{
				final short magic_number = in.readShort();
				clazz = ClassConfigurator.get(magic_number);
				if (clazz == null)
				{
					throw new ClassNotFoundException("Class for magic number " + magic_number
					        + " cannot be found.");
				}
			} else
			{
				classname = in.readUTF();
				clazz = ClassConfigurator.get(classname);
				if (clazz == null)
				{
					throw new ClassNotFoundException(classname);
				}
			}

			retval = (Streamable) clazz.newInstance();
			retval.readFrom(in);
			return retval;
		} catch (final Exception ex)
		{
			throw new IOException("failed reading object: " + ex.toString());
		}
	}

	public static byte[] streamableToByteBuffer(final Streamable obj) throws Exception
	{
		byte[] result = null;
		final ByteArrayOutputStream out_stream = new ByteArrayOutputStream(512);
		final DataOutputStream out = new DataOutputStream(out_stream);
		obj.writeTo(out);
		result = out_stream.toByteArray();
		out.close();
		return result;
	}

}
