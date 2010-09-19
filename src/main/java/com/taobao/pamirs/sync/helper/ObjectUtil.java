// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.helper;

public class ObjectUtil
{
	public static Object convertToRightObject(final String theType, final Object value)
	{
		if (theType.equals("java.lang.String") || theType.equals("String"))
			return (value);
		if (theType.equals("java.lang.Integer") || theType.equals("int") || theType.equals("Integer"))
			return new Integer((String) value);
		if (theType.equals("java.lang.Boolean") || theType.equals("boolean") || theType.equals("Boolean"))
			return new Boolean((String) value);
		// if (theType.equals("java.util.Properties"))
		// return makePropertiesObject(value);
		// if (theType.equals("java.util.Map"))
		// return makeMapObject(value);
		// if ((theType.equals("java.lang.Byte")) || (theType.equals("[B")))
		// return makeByteArray(value);
		if (theType.equals("java.lang.Long") || theType.equals("long") || theType.equals("Long"))
			return makeLongObject(value);
		if (theType.equals("java.lang.Double") || theType.equals("double") || theType.equals("Double"))
		{
			return new Double((String) value);
		}

		return value;
	}

	public static Class<?> convertToRightClass(final String theType)
	{
		if (theType.equals("java.lang.String") || theType.equals("String"))
			return (String.class);
		if (theType.equals("java.lang.Integer") || theType.equals("int") || theType.equals("Integer"))
			return Integer.class;
		if (theType.equals("java.lang.Boolean") || theType.equals("boolean"))
			return Boolean.class;
		// if (theType.equals("java.util.Properties"))
		// return makePropertiesObject(value);
		// if (theType.equals("java.util.Map"))
		// return makeMapObject(value);
		// if ((theType.equals("java.lang.Byte")) || (theType.equals("[B")))
		// return makeByteArray(value);
		if (theType.equals("java.lang.Long") || theType.equals("long") || theType.equals("Long"))
			return Long.class;
		if (theType.equals("java.lang.Double") || theType.equals("double") || theType.equals("Double"))
		{
			return Double.class;
		}

		return Object.class;
	}

	public static Long makeLongObject(final Object val)
	{
		String theVal = (String) val;
		if (theVal.endsWith("L"))
		{
			theVal = theVal.replaceAll("L", "");
			final long lo = Long.parseLong(theVal);
			return new Long(lo);
		}
		final long lo1 = Long.parseLong(theVal);
		return new Long(lo1);
	}

	//
	// public static byte[] makeByteArray(final Object val)
	// {
	// final String theVal = (String) val;
	// if (theVal.startsWith("{3DES}"))
	// {
	// byte[] y = null;
	// try
	// {
	// y =
	// SerializedSystemIni.getEncryptionService().decryptBytes(theVal.getBytes());
	// }
	// catch (final Exception e)
	// {
	// return theVal.getBytes();
	// }
	// return y;
	// }
	// return theVal.getBytes();
	// }

	// public static Properties makePropertiesObject(final Object val)
	// {
	// String propString = (String) val;
	// final Properties props = new Properties();
	// if ((propString.startsWith("{")) && (propString.endsWith("}")))
	// {
	// propString = propString.substring(1, propString.length() - 1);
	// }
	// final String[] vals = splitCompletely(propString, ",");
	// for (int i = 0; i < vals.length; ++i)
	// {
	// final String[] realVals = splitCompletely(vals[i], "=");
	//
	// props.setProperty(realVals[0], realVals[1]);
	// }
	// return props;
	// }
	//
	// private Map makeMapObject(final Object val)
	// {
	// String propString = (String) val;
	// final HashMap<String, String> props = new HashMap<String, String>();
	// if ((propString.startsWith("{")) && (propString.endsWith("}")))
	// {
	// propString = propString.substring(1, propString.length() - 1);
	// }
	// final String[] vals = splitCompletely(propString, ",");
	// for (int i = 0; i < vals.length; ++i)
	// {
	// final String[] realVals = splitCompletely(vals[i], "=");
	//
	// props.put(realVals[0], realVals[1]);
	// }
	// return props;
	// }
	public static class Splitter
	{

		// the real work gets done here:
		// -- split the given string into substrings no longer than maxLen
		// -- return as an array of strings
		public static String[] split(final int maxLen, final String str)
		{

			// get the length of the original string
			final int origLen = str.length();

			// calculate the number of substrings we'll need to make
			int splitNum = origLen / maxLen;
			if (origLen % maxLen > 0)
				splitNum += 1;

			// initialize the result array
			final String[] splits = new String[splitNum];

			// for each substring...
			for (int i = 0; i < splitNum; i++)
			{

				// the substring starts here
				final int startPos = i * maxLen;

				// the substring ends here
				int endPos = startPos + maxLen;

				// make sure we don't cause an IndexOutOfBoundsException
				if (endPos > origLen)
					endPos = origLen;

				// make the substring
				final String substr = str.substring(startPos, endPos);

				// stick it in the result array
				splits[i] = substr;
			}

			// return the result array
			return splits;
		}
	}
}
